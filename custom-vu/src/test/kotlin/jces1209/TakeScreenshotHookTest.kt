package jces1209

import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.ActionResult
import jces1209.vu.TakeScreenshotHook
import org.assertj.core.api.Assertions
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import java.time.Duration
import java.time.Instant

class TakeScreenshotHookTest {
    @Rule
    @JvmField
    var folder = TemporaryFolder()

    @Test
    fun shouldRespectLimit() {
        val display = TakeScreenshotMock()
        val resultsDirectory = folder.newFolder()
        val hook = TakeScreenshotHook.Builder(
            display
        ).directory(resultsDirectory)
            .limit(2)
            .build()

        repeat(100) {
            hook.run(ActionMetric.Builder("action1", ActionResult.OK, Duration.ZERO, Instant.now()))
            hook.run(ActionMetric.Builder("action2", ActionResult.OK, Duration.ZERO, Instant.now()))
            hook.run(ActionMetric.Builder("action3", ActionResult.OK, Duration.ZERO, Instant.now()))
        }

        val results = resultsDirectory.listFiles()!!.map { it.name }
        Assertions.assertThat(results).hasSize(6)
        Assertions.assertThat(results.filter { it.contains("action1") }).hasSize(2)
        Assertions.assertThat(results.filter { it.contains("action2") }).hasSize(2)
        Assertions.assertThat(results.filter { it.contains("action3") }).hasSize(2)
    }

    private class TakeScreenshotMock(
    ) : TakesScreenshot {
        private val tmp = TemporaryFolder()

        init {
            tmp.create()
        }

        override fun <X : Any?> getScreenshotAs(target: OutputType<X>?): X {
            @Suppress("UNCHECKED_CAST")
            return tmp.newFile() as X
        }
    }
}
