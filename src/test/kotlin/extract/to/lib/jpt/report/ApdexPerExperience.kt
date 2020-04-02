package extract.to.lib.jpt.report

import com.atlassian.performance.tools.io.api.ensureParentDirectory
import com.atlassian.performance.tools.report.api.result.EdibleResult
import com.atlassian.performance.tools.workspace.api.TaskWorkspace
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.math.RoundingMode

class ApdexPerExperience(
    private val apdex: Apdex
) {

    fun report(
        results: List<EdibleResult>,
        workspace: TaskWorkspace
    ) {
        val output = workspace.isolateReport("apdex-per-exp.csv")
        output.toFile().ensureParentDirectory().bufferedWriter().use {
            reportCsv(tabularize(results), it)
        }
    }

    private fun tabularize(
        results: List<EdibleResult>
    ): ApdexTable {
        val cells = results.flatMap { tabularize(it) }
        return ApdexTable(cells)
    }

    private fun tabularize(
        result: EdibleResult
    ): List<ApdexCell> {
        val cohort = result.cohort
        val allMetrics = result.actionMetrics
        val experienceCells = allMetrics
            .groupBy { it.label }
            .map { (label, metrics) ->
                val score = apdex.score(metrics)
                ApdexCell(cohort, label, score)
            }
        val overallCell = ApdexCell(cohort, "~OVERALL", apdex.score(allMetrics))
        return experienceCells + overallCell
    }

    private fun reportCsv(
        table: ApdexTable,
        target: Appendable
    ) {
        val experiences = table.experiences()
        val headers = arrayOf("cohort") + experiences
        val format = CSVFormat.DEFAULT.withHeader(*headers).withRecordSeparator('\n')
        val printer = CSVPrinter(target, format)
        val rows = table.rowByCohorts()
        val records = rows.map { listOf(it.cohort) + it.toScoreRecords() }
        printer.printRecords(records)
        printer.flush()
    }

    private data class ApdexTable(
        val cells: List<ApdexCell>
    ) {
        fun experiences(): List<String> = cells
            .map { it.experience }
            .toSet()
            .sorted()

        fun rowByCohorts(): List<ApdexRow> = cells
            .groupBy { it.cohort }
            .map { (cohort, cells) -> ApdexRow(cohort, cells.sortedBy { it.experience }) }
    }

    private data class ApdexRow(
        val cohort: String,
        val cells: List<ApdexCell>
    ) {
        fun toScoreRecords(): List<String> = cells.map { it.toScoreRecord() }
    }

    private data class ApdexCell(
        val cohort: String,
        val experience: String,
        val score: Float
    ) {
        fun toScoreRecord(): String = score
            .toBigDecimal()
            .setScale(2, RoundingMode.HALF_UP)
            .toString()
    }
}
