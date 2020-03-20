package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.SEARCH_WITH_JQL
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.JqlMemory
import com.atlassian.performance.tools.jiraactions.api.observation.SearchJqlObservation
import com.atlassian.performance.tools.jiraactions.api.page.IssueNavigatorPage
import jces1209.vu.page.CloudIssueNavigator
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
                CloudIssueNavigator(jira.driver).waitForNavigator()
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
}
