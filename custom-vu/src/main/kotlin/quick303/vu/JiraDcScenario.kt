package quick303.vu

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.action.BrowseProjectsAction
import com.atlassian.performance.tools.jiraactions.api.action.SearchJqlAction
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import com.atlassian.performance.tools.jiraactions.api.scenario.JiraCoreScenario
import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import org.openqa.selenium.By
import quick303.vu.action.CreateAnIssue
import quick303.vu.page.DcIssuePage

class JiraDcScenario : Scenario {

    override fun getLogInAction(
        jira: WebJira,
        meter: ActionMeter,
        userMemory: UserMemory
    ): Action {
        return JiraCoreScenario().getLogInAction(jira, meter, userMemory)
    }

    override fun getActions(
        jira: WebJira,
        seededRandom: SeededRandom,
        meter: ActionMeter
    ): List<Action> {
        val similarities = ScenarioSimilarities(jira, seededRandom, meter)
        return similarities.assembleScenario(
            issuePage = DcIssuePage(jira.driver),
            createIssue = CreateAnIssue(
                jira = jira,
                meter = meter,
                projectMemory = similarities.projectMemory,
                createIssueButton = By.id("create_link")
            ),
            searchWithJql = SearchJqlAction(
                jira = jira,
                meter = meter,
                jqlMemory = similarities.jqlMemory,
                issueKeyMemory = similarities.issueKeyMemory
            ),
            browseProjects = BrowseProjectsAction(
                jira = jira,
                meter = meter,
                projectMemory = similarities.projectMemory
            )
        )
    }
}
