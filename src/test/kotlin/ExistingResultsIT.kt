import com.atlassian.performance.tools.jiraactions.api.format.MetricCompactJsonFormat
import com.atlassian.performance.tools.report.api.FullTimeline
import com.atlassian.performance.tools.report.api.result.EdibleResult
import com.atlassian.performance.tools.report.api.result.RawCohortResult
import com.atlassian.performance.tools.workspace.api.RootWorkspace
import com.atlassian.performance.tools.workspace.api.TaskWorkspace
import extract.to.lib.jpt.report.reportByActionResult
import org.junit.Test
import java.nio.file.Paths

class ExistingResultsIT {

    private val existingResultsDir = "2020-04-01T11-28-28.99364"

    @Test
    fun shouldOnlyProcessGatheredData() {
        val taskWorkspace = RootWorkspace(Paths.get("build")).isolateTask(existingResultsDir)
        val alphaProps = CohortProperties.load("a.properties")
        val betaProps = CohortProperties.load("b.properties")
        val alpha = processResults(alphaProps.cohort, taskWorkspace)
        val beta = processResults(betaProps.cohort, taskWorkspace)

        reportByActionResult(alpha, beta, taskWorkspace)
    }

    private fun processResults(
        cohort: String,
        taskWorkspace: TaskWorkspace
    ): EdibleResult = RawCohortResult.Factory()
        .fullResult(
            cohort,
            taskWorkspace
                .directory
                .resolve("vu-results")
                .resolve(cohort),
            MetricCompactJsonFormat()
        )
        .prepareForJudgement(FullTimeline())
}
