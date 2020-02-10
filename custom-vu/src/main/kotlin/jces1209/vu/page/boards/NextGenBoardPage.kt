package jces1209.vu.page.boards

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions.numberOfElementsToBeMoreThan
import java.net.URI

class NextGenBoardPage(
    private val driver: WebDriver,
    override val uri: URI
) : BoardPage {

    override fun waitForAnyIssue(): BoardContent {
        driver.get(uri.toString())
        closeModals()
        val issueCards = awaitIssueCards()
        return NextGenBoardContent(issueCards)
    }

    private fun awaitIssueCards(): List<WebElement> {
        val issueCardLocator = By.cssSelector("[data-test-id='platform-board-kit.ui.card.card']")
        return driver.wait(numberOfElementsToBeMoreThan(issueCardLocator, 0))
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
