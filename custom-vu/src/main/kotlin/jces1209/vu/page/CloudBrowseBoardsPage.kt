package jces1209.vu.page

import com.atlassian.performance.tools.jiraactions.api.page.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated
import java.time.Duration

class CloudBrowseBoardsPage(
    private val driver: WebDriver
) {
    private val tableLocator = By.cssSelector(
        "[data-test-id='global-pages.directories.directory-base.content.table.container']"
    )

    fun waitForBoards(): CloudBoardList {
        val tableElement = driver.wait(
            condition = visibilityOfElementLocated(tableLocator),
            timeout = Duration.ofSeconds(25)
        )
        return CloudBoardList(tableElement, driver)
    }
}
