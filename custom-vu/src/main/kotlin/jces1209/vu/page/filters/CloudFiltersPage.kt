package jces1209.vu.page.filters

import com.atlassian.performance.tools.jiraactions.api.WebJira
import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated

class CloudFiltersPage(
    private val jira: WebJira,
    private val driver: WebDriver
) : FiltersPage {

    private val table = By.cssSelector("[data-test-id='global-pages.directories.directory-base.content.table.container']")

    override fun open(): FiltersPage {
        jira.navigateTo("secure/ManageFilters.jspa?sortKey=popularity&sortOrder=DESC&filterView=search")
        return this
    }

    override fun waitForList(): FiltersList {
        val element = driver.wait(visibilityOfElementLocated(table))
        return CloudFiltersList(element)
    }
}
