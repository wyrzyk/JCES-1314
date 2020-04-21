import com.atlassian.performance.tools.concurrency.api.AbruptExecutorService
import com.atlassian.performance.tools.concurrency.api.submitWithLogContext
import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import com.atlassian.performance.tools.report.api.FullReport
import com.atlassian.performance.tools.report.api.FullTimeline
import com.atlassian.performance.tools.report.api.WaterfallHighlightReport
import com.atlassian.performance.tools.report.api.result.EdibleResult
import com.atlassian.performance.tools.report.api.result.RawCohortResult
import com.atlassian.performance.tools.virtualusers.api.VirtualUserOptions
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserBehavior
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserTarget
import com.atlassian.performance.tools.workspace.api.RootWorkspace
import com.atlassian.performance.tools.workspace.api.TestWorkspace
import jces1209.BenchmarkQuality
import jces1209.SlowAndMeaningful
import jces1209.log.LogConfigurationFactory
import jces1209.vu.JiraCloudScenario
import org.apache.logging.log4j.core.config.ConfigurationFactory
import org.junit.Test
import java.net.URI
import java.nio.file.Paths
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors.newCachedThreadPool

class JiraPerformanceComparisonIT {

    private val workspace = RootWorkspace(Paths.get("build")).currentTask
    private val quality: BenchmarkQuality = SlowAndMeaningful
        .Builder()
        .ramp(Duration.ofMinutes(150))
        .flat(Duration.ofMinutes(150))
        .build()

    init {
        ConfigurationFactory.setConfigurationFactory(LogConfigurationFactory(workspace))
    }

    @Test
    fun shouldComparePerformance() {
        val results: List<EdibleResult> = AbruptExecutorService(newCachedThreadPool()).use { pool ->
            listOf(
                benchmark(JiraCloudScenario::class.java, quality, pool)
            )
                .map { it.get() }
                .map { it.prepareForJudgement(FullTimeline()) }
        }
        FullReport().dump(results, workspace.isolateTest("Compare"))
        dumpMegaSlowWaterfalls(results)
    }

    private fun benchmark(
        scenario: Class<out Scenario>,
        quality: BenchmarkQuality,
        pool: ExecutorService
    ): CompletableFuture<RawCohortResult> {
        return pool.submitWithLogContext("experiment") {
            benchmark( scenario, quality)
        }
    }

    private fun benchmark(
        scenario: Class<out Scenario>,
        quality: BenchmarkQuality
    ): RawCohortResult {
        val options = loadOptions(scenario)
        val cohort = "experiment"
        val resultsTarget = workspace.directory.resolve("vu-results").resolve(cohort)
        val provisioned = quality
            .provide()
            .obtainVus(resultsTarget, workspace.directory)
        val virtualUsers = provisioned.virtualUsers
        return try {
            virtualUsers.applyLoad(options)
            virtualUsers.gatherResults()
            RawCohortResult.Factory().fullResult(cohort, resultsTarget)
        } catch (e: Exception) {
            virtualUsers.gatherResults()
            RawCohortResult.Factory().failedResult(cohort, resultsTarget, e)
        } finally {
            provisioned.resource.release().get()
        }
    }

    private fun loadOptions(
        scenario: Class<out Scenario>
    ): VirtualUserOptions {
        val target = VirtualUserTarget(
            webApplication = URI(System.getenv("bamboo_JPT_JIRA_URI")!!),
            userName = System.getenv("bamboo_JPT_USER_NAME")!!,
            password = System.getenv("bamboo_JPT_USER_PASSWORD")!!
        )

        val behavior = quality.behave(scenario)
            .let { VirtualUserBehavior.Builder(it) }
            .build()
        return VirtualUserOptions(target, behavior)
    }

    private fun dumpMegaSlowWaterfalls(
        results: List<EdibleResult>
    ) {
        results.forEach { result ->
            val megaSlow = result.actionMetrics.filter { it.duration > Duration.ofMinutes(1) }
            WaterfallHighlightReport().report(
                metrics = megaSlow,
                workspace = workspace
                    .isolateTest("Mega slow")
                    .directory
                    .resolve(result.cohort)
                    .let { TestWorkspace(it) }
            )
        }
    }
}
