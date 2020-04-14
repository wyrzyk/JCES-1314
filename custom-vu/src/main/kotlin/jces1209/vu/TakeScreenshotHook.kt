package jces1209.vu

import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.measure.PostMetricHook
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import java.io.File
import java.nio.file.Files
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Takes post metric screenshots. This can help to identify how Jira looks after the action finished.
 */
class TakeScreenshotHook private constructor(
    private val display: TakesScreenshot,
    private val directory: File,
    private val limit: Int
) : PostMetricHook {
    private val counterPerAction = ConcurrentHashMap<String, AtomicInteger>()

    override fun run(actionMetricBuilder: ActionMetric.Builder) {
        val actionMetric = actionMetricBuilder.build()
        val counter = counterPerAction.computeIfAbsent(actionMetric.label) {
            AtomicInteger()
        }.incrementAndGet()
        if (counter <= limit) {
            saveScreenshot(
                directory
                    .resolve(actionMetric.label + "-" + counter)
            )
        }
    }

    private fun saveScreenshot(
        screenshot: File
    ): String {
        val temporaryScreenshot = display.getScreenshotAs(OutputType.FILE)
        val moved = temporaryScreenshot.renameTo(screenshot)
        return when {
            moved -> "screenshot saved to ${screenshot.path}"
            else -> "screenshot failed to migrate from ${temporaryScreenshot.path}"
        }
    }

    class Builder(
        private val display: TakesScreenshot
    ) {
        private var directory: File = File(".")
            .resolve("test-results")
        private var limit: Int = 3

        fun directory(directory: File) = apply { this.directory = directory }
        fun limit(limit: Int) = apply { this.limit = limit }

        fun build(): TakeScreenshotHook {
            Files.createDirectories(directory.toPath())
            return TakeScreenshotHook(
                display,
                directory,
                limit
            )
        }
    }
}
