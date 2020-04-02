package extract.to.lib.jpt.report

import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import extract.to.lib.jpt.report.Apdex.Satisfaction.FRUSTRATED
import extract.to.lib.jpt.report.Apdex.Satisfaction.SATISFACTORY
import extract.to.lib.jpt.report.Apdex.Satisfaction.TOLERATING
import java.time.Duration

class Apdex(
    private val maxSatisfaction: Duration,
    private val maxTolerance: Duration
) {

    constructor() : this(
        Duration.ofSeconds(1),
        Duration.ofSeconds(4)
    )

    fun score(
        metrics: List<ActionMetric>
    ): Float = metrics
        .map { categorize(it).score }
        .average()
        .toFloat()

    private fun categorize(
        metric: ActionMetric
    ): Satisfaction = when {
        metric.duration < maxSatisfaction -> SATISFACTORY
        metric.duration < maxTolerance -> TOLERATING
        else -> FRUSTRATED
    }

    private enum class Satisfaction(
        val score: Float
    ) {
        SATISFACTORY(1.0f),
        TOLERATING(0.5f),
        FRUSTRATED(0.0f)
    }
}

