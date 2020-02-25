import com.atlassian.performance.tools.concurrency.api.submitWithLogContext
import com.atlassian.performance.tools.report.api.FullReport
import com.atlassian.performance.tools.report.api.FullTimeline
import com.atlassian.performance.tools.report.api.result.RawCohortResult
import com.atlassian.performance.tools.virtualusers.api.VirtualUserOptions
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserBehavior
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserTarget
import com.atlassian.performance.tools.workspace.api.RootWorkspace
import jces1209.BenchmarkQuality
import jces1209.QuickAndDirty
import jces1209.SlowAndMeaningful
import jces1209.vu.JiraCloudScenario
import org.junit.Test
import java.io.File
import java.net.URI
import java.nio.file.Paths
import java.util.concurrent.Executors

class JiraPerformanceComparisonIT {

    private val workspace = RootWorkspace(Paths.get("build")).currentTask
    private val benchmarkQuality: BenchmarkQuality = SlowAndMeaningful()

    @Test
    fun shouldComparePerformance() {
        val pool = Executors.newCachedThreadPool()
        val baseline = pool.submitWithLogContext("baseline") {
            benchmark(File("jira-baseline.properties"))
        }
        val experiment = pool.submitWithLogContext("experiment") {
            benchmark(File("jira-experiment.properties"))
        }
        FullReport().dump(
            results = listOf(baseline, experiment).map { it.get().prepareForJudgement(FullTimeline()) },
            workspace = workspace.isolateTest("Compare")
        )
    }

    private fun benchmark(
        propertiesFile: File
    ): RawCohortResult {
        val properties = CohortProperties.load(propertiesFile)
        val options = loadOptions(properties)
        val cohort = properties.cohort
        val resultsTarget = workspace.directory.resolve("vu-results").resolve(cohort)
        val provisioned = benchmarkQuality
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

    private fun loadOptions(properties: CohortProperties): VirtualUserOptions {
        val target = VirtualUserTarget(
            webApplication = properties.jira,
            userName = properties.userName,
            password = properties.userPassword
        )
        val behavior = benchmarkQuality.behave(JiraCloudScenario::class.java)
            .let { VirtualUserBehavior.Builder(it) }
            .avoidLeakingPersonalData(properties.jira)
            .build()
        return VirtualUserOptions(target, behavior)
    }

    private fun VirtualUserBehavior.Builder.avoidLeakingPersonalData(
        uri: URI
    ) = apply {
        if (uri.host.endsWith("atlassian.net")) {
            diagnosticsLimit(0)
        }
    }
}
