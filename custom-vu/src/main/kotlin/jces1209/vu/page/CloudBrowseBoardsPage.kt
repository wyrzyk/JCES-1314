package jces1209.vu.page

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions

class CloudBrowseBoardsPage(
    private val driver: WebDriver
) {
    private val tableLocator = By.cssSelector(
        "[data-test-id='global-pages.directories.directory-base.content.table.container']"
    )

    fun waitForBoards(): CloudBoardList {
        val tableElement = driver.wait(
            ExpectedConditions.visibilityOfElementLocated(tableLocator)
        )
        return CloudBoardList(tableElement, driver)
    }
}
