package jces1209.vu.page.boards

import jces1209.vu.page.FalliblePage
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import java.net.URI

class NextGenBoardPage(
    private val driver: WebDriver,
    override val uri: URI
) : BoardPage {

    private val falliblePage = FalliblePage.Builder(
        webDriver = driver,
        expectedContent = listOf(
            By.xpath("//*[contains(text(), 'Your board has too many issues')]"),
            By.xpath("//*[contains(text(), 'Board not accessible')]"),
            By.cssSelector("[data-test-id='platform-board-kit.common.ui.column-header.header.column-header-container']")
        )
    )
        .cloudErrors()
        .build()

    override fun waitForBoardPageToLoad(): BoardContent {
        driver.get(uri.toString())
        closeModals()

        falliblePage.waitForPageToLoad()

        val issueCards = findIssueCards()
        return NextGenBoardContent(issueCards)
    }

    private fun findIssueCards(): List<WebElement> {
        val issueCardLocator = By.cssSelector("[data-test-id='platform-board-kit.ui.card.card']")
        return driver.findElements(issueCardLocator)
    }

    private fun closeModals() {
        driver
            .findElements(By.cssSelector("[aria-label='Close Modal']"))
            .forEach { it.click() }
    }

    private class NextGenBoardContent(
        private val issueCards: List<WebElement>
    ) : BoardContent {

        override fun getIssueCount(): Int = issueCards.size

        /**
         * The new front-end is hostile towards automatic data discovery, so we give up.
         */
        override fun getIssueKeys(): Collection<String> = emptyList()
    }
}
