import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.ActionResult
import com.atlassian.performance.tools.jiraactions.api.format.MetricCompactJsonFormat
import com.atlassian.performance.tools.jiraactions.api.format.MetricJsonFormat
import com.atlassian.performance.tools.report.api.FullReport
import com.atlassian.performance.tools.report.api.FullTimeline
import com.atlassian.performance.tools.report.api.Timeline
import com.atlassian.performance.tools.report.api.result.EdibleResult
import com.atlassian.performance.tools.report.api.result.RawCohortResult
import com.atlassian.performance.tools.workspace.api.RootWorkspace
import org.junit.Test
import java.io.File
import java.nio.file.Paths

class ExistingResultsIT {

    private val workspace = RootWorkspace(Paths.get("build"))
    private val taskIdToProcess = "2020-03-06T14-00-16.448"


    @Test
    fun shouldOnlyProcessGatheredData() {
        val baselineProperties = CohortProperties.load(File("jira-baseline.properties"))
        val experimentProperties = CohortProperties.load(File("jira-experiment.properties"))
        val metricFormat = MetricCompactJsonFormat()
        val timeline = FullTimeline()

        val baselineResult = processResults(
            baselineProperties.cohort,
            metricFormat,
            timeline
        )

        val experimentResult = processResults(
            experimentProperties.cohort,
            metricFormat,
            timeline
        )

        reportByActionResult(baselineResult, experimentResult)
    }



    private fun reportByActionResult(baselineResult: EdibleResult, experimentResult: EdibleResult) {
        FullReport().dump(
            results = splitByActionResult(baselineResult) + splitByActionResult(experimentResult),
            workspace = workspace.isolateTask(taskIdToProcess).isolateTest("By ActionResult")
        )
    }

    private fun splitByActionResult(edibleResult: EdibleResult): List<EdibleResult> {
        val result = ActionResult.values()
            .map { it to mutableListOf<ActionMetric>() }
            .toMap()
        edibleResult.actionMetrics.forEach { (result[it.result] ?: error("Invalid result")).add(it) }

        return result.entries.map { it.value.toEdibleResult("${edibleResult.cohort} ${it.key.name}") }
    }

    private fun processResults(
        cohort: String,
        metricJsonFormat: MetricJsonFormat,
        timeline: Timeline
    ): EdibleResult = RawCohortResult.Factory()
        .fullResult(
            cohort,
            workspace.directory
                .resolve(taskIdToProcess)
                .resolve("vu-results")
                .resolve(cohort),
            metricJsonFormat
        )
        .prepareForJudgement(timeline)


    private fun List<ActionMetric>.toEdibleResult(name: String)
        : EdibleResult = EdibleResult.Builder(name).actionMetrics(this).build()
}
