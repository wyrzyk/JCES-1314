package jces1209.vu.page.boards

import jces1209.vu.page.FalliblePage
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import java.net.URI

internal class ClassicBoardPage(
    private val driver: WebDriver,
    override val uri: URI
) : BoardPage {

    private val falliblePage = FalliblePage.Builder(
        webDriver = driver,
        expectedContent = listOf(
            By.xpath("//*[contains(text(), 'Your board has too many issues')]"),
            By.xpath("//*[contains(text(), 'Board not accessible')]"),
            By.xpath("//*[contains(text(), 'Set a new location for your board')]"),
            By.cssSelector(".ghx-column")
        )
    )
        .cloudErrors()
        .build()

    override fun waitForBoardPageToLoad(): BoardContent {
        falliblePage.waitForPageToLoad()
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
