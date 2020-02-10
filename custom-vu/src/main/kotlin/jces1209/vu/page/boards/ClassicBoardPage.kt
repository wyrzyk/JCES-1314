package jces1209.vu.page.boards

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated
import java.net.URI

internal class ClassicBoardPage(
    private val driver: WebDriver,
    override val uri: URI
) : BoardPage {

    override fun waitForAnyIssue(): BoardContent {
        driver.wait(
            presenceOfElementLocated(By.cssSelector(".ghx-column"))
        )
        return KanbanBoardContent(driver)
    }

    private class KanbanBoardContent(
        private val driver: WebDriver
    ) : BoardContent {

        private val lazyIssueKeys: Collection<String> by lazy {
            driver
                .findElements(By.className("ghx-issue"))
                .map { it.getAttribute("data-issue-key") }
        }

        override fun getIssueCount(): Int = lazyIssueKeys.size
        override fun getIssueKeys(): Collection<String> = lazyIssueKeys
    }
}
