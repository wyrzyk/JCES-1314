package quick303.vu

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.action.ProjectSummaryAction
import com.atlassian.performance.tools.jiraactions.api.action.ViewDashboardAction
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveIssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveJqlMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveProjectMemory
import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import com.atlassian.performance.tools.jiraactions.api.scenario.addMultiple
import quick303.vu.action.*

class JiraCloudScenario : Scenario {

    override fun getLogInAction(
        jira: WebJira,
        meter: ActionMeter,
        userMemory: UserMemory
    ): Action {
        return JiraCloudLogIn(userMemory, jira)
    }

    override fun getActions(
        jira: WebJira,
        seededRandom: SeededRandom,
        meter: ActionMeter
    ): List<Action> {
        val issueKeyMemory = AdaptiveIssueKeyMemory(seededRandom)
        val projectMemory = AdaptiveProjectMemory(seededRandom)
        val jqlMemory = AdaptiveJqlMemory(seededRandom)
        jqlMemory.remember(listOf("order by created DESC")) // work around https://ecosystem.atlassian.net/browse/JPERF-573
        jqlMemory.remember(listOf("text ~ lorem"))
        val scenario: MutableList<Action> = mutableListOf()
        val createIssue = CreateCloudIssue(
            jira = jira,
            meter = meter,
            projectMemory = projectMemory
        )
        val searchWithJql = SearchCloudJql(
            jira = jira,
            meter = meter,
            jqlMemory = jqlMemory,
            issueKeyMemory = issueKeyMemory
        )
        val viewIssue = WorkWithCloudIssue(
            jira = jira,
            meter = meter,
            issueKeyMemory = issueKeyMemory,
            random = seededRandom,
            editProbability = 0.10f,
            commentProbability = 1.00f // 0.04f
        )
        val projectSummary = ProjectSummaryAction(
            jira = jira,
            meter = meter,
            projectMemory = projectMemory
        )
        val viewDashboard = ViewDashboardAction(
            jira = jira,
            meter = meter
        )
        val browseProjects = BrowseCloudProjects(
            jira = jira,
            meter = meter,
            projectMemory = projectMemory
        )

        val actionProportions = mapOf(
            createIssue to 5,
            searchWithJql to 20,
            viewIssue to 55,
            projectSummary to 5,
            viewDashboard to 10,
            browseProjects to 5
        )

        actionProportions.entries.forEach { scenario.addMultiple(element = it.key, repeats = it.value) }
        scenario.shuffle(seededRandom.random)
        return scenario
    }
}
