package jces1209.vu.page.filters

import com.atlassian.performance.tools.jiraactions.api.WebJira
import jces1209.vu.page.FalliblePage
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

class CloudFiltersPage(
    private val jira: WebJira,
    private val driver: WebDriver
) : FiltersPage {

    private val table = By.cssSelector("[data-test-id='global-pages.directories.directory-base.content.table.container']")
    private val falliblePage = FalliblePage.Builder(
        expectedContent = listOf(table),
        webDriver = driver
    )
        .cloudErrors()
        .build()

    override fun open(): FiltersPage {
        jira.navigateTo("secure/ManageFilters.jspa?sortKey=popularity&sortOrder=DESC&filterView=search")
        return this
    }

    override fun waitForList(): FiltersList {
        falliblePage.waitForPageToLoad()
        return CloudFiltersList(driver.findElement(table))
    }
}
