package jces1209.vu.page.filters

import com.atlassian.performance.tools.jiraactions.api.WebJira
import jces1209.vu.page.FalliblePage
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

class ServerFiltersPage(
    private val jira: WebJira,
    private val driver: WebDriver
) : FiltersPage {

    private val table = By.id("mf_popular")
    private val falliblePage = FalliblePage.Builder(
        expectedContent = listOf(table),
        webDriver = driver
    )
        .serverErrors()
        .build()

    override fun open(): FiltersPage {
        jira.navigateTo("secure/ManageFilters.jspa?filterView=popular")
        return this
    }

    override fun waitForList(): FiltersList {
        falliblePage.waitForPageToLoad()
        return ServerFiltersList(driver.findElement(table))
    }
}
