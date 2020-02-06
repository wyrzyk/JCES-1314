package jces1209.vu

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.action.ProjectSummaryAction
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveIssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveJqlMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveProjectMemory
import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import com.atlassian.performance.tools.jirasoftwareactions.api.WebJiraSoftware
import com.atlassian.performance.tools.jirasoftwareactions.api.actions.ViewBoardAction
import com.atlassian.performance.tools.jirasoftwareactions.api.boards.AgileBoard
import com.atlassian.performance.tools.jirasoftwareactions.api.memories.AdaptiveBoardMemory
import jces1209.vu.action.BrowseCloudBoards
import jces1209.vu.action.BrowseCloudProjects
import jces1209.vu.action.CreateAnIssue
import jces1209.vu.action.JiraCloudLogIn
import jces1209.vu.action.SearchCloudJql
import jces1209.vu.action.WorkAnIssue
import jces1209.vu.page.CloudIssuePage
import org.openqa.selenium.By
import java.util.Collections

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
        val jqlMemory = AdaptiveJqlMemory(seededRandom)
            .also { it.remember(listOf("order by created DESC")) } // work around https://ecosystem.atlassian.net/browse/JPERF-573
        val issueKeyMemory = AdaptiveIssueKeyMemory(seededRandom)
        val projectMemory = AdaptiveProjectMemory(seededRandom)
        val issuePage = CloudIssuePage(jira.driver)
        val createIssue = CreateAnIssue(
            jira = jira,
            meter = meter,
            projectMemory = projectMemory,
            createIssueButton = By.id("createGlobalItem")
        )
        val searchWithJql = SearchCloudJql(
            jira = jira,
            meter = meter,
            jqlMemory = jqlMemory,
            issueKeyMemory = issueKeyMemory
        )
        val browseProjects = BrowseCloudProjects(
            jira = jira,
            meter = meter,
            projectMemory = projectMemory
        )
        val workAnIssue = WorkAnIssue(
            issuePage = issuePage,
            jira = jira,
            meter = meter,
            issueKeyMemory = issueKeyMemory,
            random = seededRandom,
            commentProbability = 0.04f
        )
        val projectSummary = ProjectSummaryAction(
            jira = jira,
            meter = meter,
            projectMemory = projectMemory
        )
        val jsw = WebJiraSoftware(jira)
        val agileBoardMemory = AdaptiveBoardMemory<AgileBoard>(seededRandom)
        val browseBoards = BrowseCloudBoards(
            jira = jira,
            meter = meter,
            boardsMemory = agileBoardMemory
        )
        val viewBoard = ViewBoardAction(
            jiraSoftware = jsw,
            meter = meter,
            boardMemory = agileBoardMemory,
            issueKeyMemory = issueKeyMemory
        )
        return mapOf(
            createIssue to 5,
            searchWithJql to 20,
            workAnIssue to 55,
            projectSummary to 5,
            browseProjects to 5,
            browseBoards to 5,
            viewBoard to 30
        )
            .map { (action, proportion) -> Collections.nCopies(proportion, action) }
            .flatten()
            .shuffled(seededRandom.random)
    }
}
