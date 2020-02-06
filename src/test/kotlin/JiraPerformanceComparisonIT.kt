import com.atlassian.performance.tools.concurrency.api.submitWithLogContext
import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import com.atlassian.performance.tools.report.api.FullReport
import com.atlassian.performance.tools.report.api.FullTimeline
import com.atlassian.performance.tools.report.api.result.RawCohortResult
import com.atlassian.performance.tools.virtualusers.api.VirtualUserOptions
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserTarget
import com.atlassian.performance.tools.workspace.api.RootWorkspace
import org.junit.Test
import quick303.BenchmarkQuality
import quick303.QuickAndDirty
import quick303.vu.JiraCloudScenario
import java.io.File
import java.net.URI
import java.nio.file.Paths
import java.util.Properties
import java.util.concurrent.Executors

class JiraPerformanceComparisonIT {

    private val workspace = RootWorkspace(Paths.get("build")).currentTask

    @Test
    fun shouldComparePerformance() {
        val benchmarkQuality: BenchmarkQuality = QuickAndDirty()
        val pool = Executors.newCachedThreadPool()
        val baseline = pool.submitWithLogContext("baseline") {
            benchmark(
                cohort = "Hello",
                target = loadTarget(File("jira-baseline.properties")),
                scenario = JiraCloudScenario::class.java,
                benchmarkQuality = benchmarkQuality
            )
        }
        val experiment = pool.submitWithLogContext("experiment") {
            benchmark(
                cohort = "10k",
                target = loadTarget(File("jira-experiment.properties")),
                scenario = JiraCloudScenario::class.java,
                benchmarkQuality = benchmarkQuality
            )
        }
        FullReport().dump(
            results = listOf(baseline, experiment).map { it.get().prepareForJudgement(FullTimeline()) },
            workspace = workspace.isolateTest("Compare")
        )
    }

    private fun loadTarget(properties: File): VirtualUserTarget {
        val jiraCloud = Properties()
        properties.bufferedReader().use { jiraCloud.load(it) }
        return VirtualUserTarget(
            webApplication = URI(jiraCloud.getProperty("jira.uri")!!),
            userName = jiraCloud.getProperty("user.name")!!,
            password = jiraCloud.getProperty("user.password")!!
        )
    }

    private fun benchmark(
        cohort: String,
        target: VirtualUserTarget,
        scenario: Class<out Scenario>,
        benchmarkQuality: BenchmarkQuality
    ): RawCohortResult {
        val resultsTarget = workspace.directory.resolve("vu-results").resolve(cohort)
        val options = VirtualUserOptions(target, benchmarkQuality.behave(scenario))
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
}
