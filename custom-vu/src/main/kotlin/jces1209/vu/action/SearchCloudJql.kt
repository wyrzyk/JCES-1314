package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.SEARCH_WITH_JQL
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory
import com.atlassian.performance.tools.jiraactions.api.observation.SearchJqlObservation
import com.atlassian.performance.tools.jiraactions.api.page.IssueNavigatorPage
import com.atlassian.performance.tools.jiraactions.api.page.JiraErrors
import com.atlassian.performance.tools.jiraactions.api.page.wait
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions.and
import org.openqa.selenium.support.ui.ExpectedConditions.or
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated
import java.time.Duration
import javax.json.JsonObject

class SearchCloudJql(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val jqlMemory: JqlMemory,
    private val issueKeyMemory: IssueKeyMemory
) : Action {
    override fun run() {
        val jqlQuery = jqlMemory.recall()!!
        meter.measure(
            key = SEARCH_WITH_JQL,
            action = fun(): IssueNavigatorPage {
                val issueNavigator = jira.goToIssueNavigator(jqlQuery)
                waitForIssueNavigator()
                return issueNavigator
            },
            observation = fun(navigator: IssueNavigatorPage): JsonObject {
                val issueKeys = navigator.getIssueKeys()
                issueKeyMemory.remember(issueKeys)
                return SearchJqlObservation(
                    navigator.jql,
                    issueKeys.size,
                    -1 // work around https://ecosystem.atlassian.net/browse/JPERF-605
                ).serialize()
            }
        )
    }

    private fun waitForIssueNavigator() {
        val driver = jira.driver
        val jiraErrors = JiraErrors(driver)
        driver.wait(
            Duration.ofSeconds(30),
            or(
                and(
                    or(
                        presenceOfElementLocated(By.cssSelector("ol.issue-list")),
                        presenceOfElementLocated(By.id("issuetable")),
                        presenceOfElementLocated(By.id("issue-content"))
                    ),
                    or(
                        presenceOfElementLocated(By.id("jira-issue-header")),
                        presenceOfElementLocated(By.id("key-val"))
                    ),
                    or(
                        presenceOfElementLocated(By.id("new-issue-body-container")),
                        presenceOfElementLocated(By.className("issue-body-content"))
                    )
                ),
                presenceOfElementLocated(By.className("no-results-hint")),
                jiraErrors.anyCommonError()
            )
        )
    }
}
