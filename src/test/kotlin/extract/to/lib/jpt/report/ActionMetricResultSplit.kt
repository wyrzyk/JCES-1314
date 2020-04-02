package extract.to.lib.jpt.report

import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.ActionResult
import com.atlassian.performance.tools.report.api.FullReport
import com.atlassian.performance.tools.report.api.result.EdibleResult
import com.atlassian.performance.tools.workspace.api.TaskWorkspace

fun reportByActionResult(baselineResult: EdibleResult, experimentResult: EdibleResult, taskWorkspace: TaskWorkspace) {
    FullReport().dump(
        results = splitByActionResult(baselineResult) + splitByActionResult(experimentResult),
        workspace = taskWorkspace.isolateTest("By ActionResult")
    )
}

private fun splitByActionResult(edibleResult: EdibleResult): List<EdibleResult> {
    val result = ActionResult.values()
        .map { it to mutableListOf<ActionMetric>() }
        .toMap()
    edibleResult
        .actionMetrics
        .forEach { (result[it.result] ?: error("Invalid result")).add(it) }
    return result
        .entries
        .map { it.value.toEdibleResult("${edibleResult.cohort} ${it.key.name}") }
}

private fun List<ActionMetric>.toEdibleResult(
    name: String
): EdibleResult = EdibleResult.Builder(name)
    .actionMetrics(this)
    .build()
