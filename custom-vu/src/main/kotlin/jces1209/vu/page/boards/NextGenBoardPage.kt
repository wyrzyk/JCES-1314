package jces1209.vu.page.boards

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions.or
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated
import java.net.URI

class NextGenBoardPage(
    private val driver: WebDriver,
    override val uri: URI
) : BoardPage {
    private val boardLoadedLocators = listOf(
        By.xpath("//*[contains(text(), 'Your board has too many issues')]"),
        By.xpath("//*[contains(text(), 'Board not accessible')]"),
        By.cssSelector("[data-test-id='platform-board-kit.common.ui.column-header.header.column-header-container']")
    )

    override fun waitForBoardPageToLoad(): BoardContent {
        driver.get(uri.toString())
        closeModals()

        driver.wait(
            or(*boardLoadedLocators.map { presenceOfElementLocated(it) }.toTypedArray())
        )

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
