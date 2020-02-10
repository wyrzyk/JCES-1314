import com.atlassian.performance.tools.concurrency.api.submitWithLogContext
import com.atlassian.performance.tools.report.api.FullReport
import com.atlassian.performance.tools.report.api.FullTimeline
import com.atlassian.performance.tools.report.api.result.RawCohortResult
import com.atlassian.performance.tools.virtualusers.api.VirtualUserOptions
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserTarget
import com.atlassian.performance.tools.workspace.api.RootWorkspace
import org.junit.Test
import jces1209.BenchmarkQuality
import jces1209.QuickAndDirty
import jces1209.vu.JiraCloudScenario
import java.io.File
import java.net.URI
import java.nio.file.Paths
import java.util.Properties
import java.util.concurrent.Executors

class JiraPerformanceComparisonIT {

    private val workspace = RootWorkspace(Paths.get("build")).currentTask
    private val benchmarkQuality: BenchmarkQuality = QuickAndDirty()

    @Test
    fun shouldComparePerformance() {
        val pool = Executors.newCachedThreadPool()
        val baseline = pool.submitWithLogContext("baseline") {
            benchmark(
                cohort = "double JDOG",
                target = loadTarget(File("jira-baseline.properties"))
            )
        }
        val experiment = pool.submitWithLogContext("experiment") {
            benchmark(
                cohort = "10k EAP",
                target = loadTarget(File("jira-experiment.properties"))
            )
        }
        FullReport().dump(
            results = listOf(baseline, experiment).map { it.get().prepareForJudgement(FullTimeline()) },
            workspace = workspace.isolateTest("Compare")
        )
    }

    private fun loadTarget(properties: File): VirtualUserTarget {
        val props = Properties()
        properties.bufferedReader().use { props.load(it) }
        return VirtualUserTarget(
            webApplication = URI(props.getProperty("jira.uri")!!),
            userName = props.getProperty("user.name")!!,
            password = props.getProperty("user.password")!!
        )
    }

    private fun benchmark(
        cohort: String,
        target: VirtualUserTarget
    ): RawCohortResult {
        val resultsTarget = workspace.directory.resolve("vu-results").resolve(cohort)
        val options = VirtualUserOptions(target, benchmarkQuality.behave(JiraCloudScenario::class.java))
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
