package quick303.vu

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import org.openqa.selenium.By
import quick303.vu.action.BrowseCloudProjects
import quick303.vu.action.CreateAnIssue
import quick303.vu.action.JiraCloudLogIn
import quick303.vu.action.SearchCloudJql
import quick303.vu.page.CloudIssuePage

class JiraCloudScenario : Scenario {

    override fun getLogInAction(
        jira: WebJira,
        meter: ActionMeter,
        userMemory: UserMemory
    ): Action {
        return JiraCloudLogIn(userMemory, jira, meter)
    }

    override fun getActions(
        jira: WebJira,
        seededRandom: SeededRandom,
        meter: ActionMeter
    ): List<Action> {
        val similarities = ScenarioSimilarities(jira, seededRandom, meter)
        return similarities.assembleScenario(
            issuePage = CloudIssuePage(jira.driver),
            createIssue = CreateAnIssue(
                jira = jira,
                meter = meter,
                projectMemory = similarities.projectMemory,
                createIssueButton = By.id("createGlobalItem")
            ),
            searchWithJql = SearchCloudJql(
                jira = jira,
                meter = meter,
                jqlMemory = similarities.jqlMemory,
                issueKeyMemory = similarities.issueKeyMemory
            ),
            browseProjects = BrowseCloudProjects(
                jira = jira,
                meter = meter,
                projectMemory = similarities.projectMemory
            )
        )
    }
}
