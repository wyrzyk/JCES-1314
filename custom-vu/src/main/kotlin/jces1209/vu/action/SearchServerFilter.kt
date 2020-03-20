package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.SEARCH_WITH_JQL
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.Memory
import com.atlassian.performance.tools.jiraactions.api.page.IssueNavigatorPage
import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions.and
import org.openqa.selenium.support.ui.ExpectedConditions.or
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated
import java.net.URI

class SearchServerFilter(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val filters: Memory<URI>
) : Action {
    override fun run() {
        val filter = filters.recall()!!
        meter.measure(
            key = SEARCH_WITH_JQL,
            action = {
                jira.navigateTo(filter.toString())
                waitForIssueNavigator()
            }
        )
    }

    /**
     * Improves [IssueNavigatorPage.waitForIssueNavigator], by avoiding the "no results" condition:
     * `presenceOfElementLocated(By.className("no-results-hint"))`
     * It seems it's present even if the results are still loading.
     * Switching to `visibilityOfElementLocated` does not help.
     */
    private fun waitForIssueNavigator() {
        jira.driver.wait(
            and(
                or(
                    presenceOfElementLocated(By.cssSelector("ol.issue-list")),
                    presenceOfElementLocated(By.id("issuetable")),
                    presenceOfElementLocated(By.id("issue-content"))
                ),
                presenceOfElementLocated(By.id("key-val")),
                presenceOfElementLocated(By.className("issue-body-content"))
            )
        )
    }
}
