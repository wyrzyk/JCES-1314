package jces1209.vu.page.boards

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.or
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated
import java.net.URI

internal class ClassicBoardPage(
    private val driver: WebDriver,
    override val uri: URI
) : BoardPage {
    private val boardLoadedLocators = listOf(
        By.xpath("//*[contains(text(), 'Your board has too many issues')]"),
        By.xpath("//*[contains(text(), 'Board not accessible')]"),
        By.xpath("//*[contains(text(), 'Set a new location for your board')]"),
        By.cssSelector(".ghx-column")
    )

    override fun waitForBoardPageToLoad(): BoardContent {
        driver.wait(
            or(*boardLoadedLocators.map { presenceOfElementLocated(it) }.toTypedArray())
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
