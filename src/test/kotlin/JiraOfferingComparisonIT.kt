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
import quick303.SlowAndMeaningful
import quick303.vu.JiraCloudScenario
import quick303.vu.JiraDcScenario
import java.io.File
import java.net.URI
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.Executors

class JiraOfferingComparisonIT {

    private val workspace = RootWorkspace(Paths.get("build")).currentTask

    @Test
    fun shouldCompareCloudWithDc() {
        val benchmarkQuality: BenchmarkQuality = SlowAndMeaningful()
        val pool = Executors.newCachedThreadPool()
        val cloudResult = pool.submitWithLogContext("Cloud") {
            benchmark(
                cohort = "Cloud",
                target = loadTarget(File("jira-cloud.properties")),
                scenario = JiraCloudScenario::class.java,
                benchmarkQuality = benchmarkQuality
            )
        }
        val dcResult = pool.submitWithLogContext("DC") {
            benchmark(
                cohort = "DC",
                target = loadTarget(File("jira-dc.properties")),
                scenario = JiraDcScenario::class.java,
                benchmarkQuality = benchmarkQuality
            )
        }
        FullReport().dump(
            results = listOf(cloudResult, dcResult).map { it.get().prepareForJudgement(FullTimeline()) },
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