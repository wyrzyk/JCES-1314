package jces1209.vu

import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.measure.output.ActionMetricOutput
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import jces1209.vu.action.BrowseCloudBoards
import jces1209.vu.action.BrowseCloudProjects
import jces1209.vu.action.CreateAnIssue
import jces1209.vu.action.LogInWithAtlassianId
import jces1209.vu.action.SearchCloudFilter
import jces1209.vu.action.ViewCloudBoard
import jces1209.vu.page.CloudIssuePage
import jces1209.vu.page.boards.BoardPage
import jces1209.vu.page.filters.CloudFiltersPage
import org.openqa.selenium.By
import java.util.*

class JiraCloudScenario : Scenario {

    override fun getLogInAction(
        jira: WebJira,
        meter: ActionMeter,
        userMemory: UserMemory
    ): Action {
        val user = userMemory
            .recall()
            ?: throw Exception("I cannot recall which user I am")
        return LogInWithAtlassianId(user, jira, meter)
    }

    override fun getActions(
        jira: WebJira,
        seededRandom: SeededRandom,
        actionMeter: ActionMeter
    ): List<Action> {
        val meter = ActionMeter
            .Builder(actionMeter)
            .overrideOutput { SkippingActionMetricOutput(50, it) }
            .build()

        val similarities = ScenarioSimilarities(jira, seededRandom, meter)
        val boardPages = SeededMemory<BoardPage>(seededRandom)
        return similarities.assembleScenario(
            issuePage = CloudIssuePage(jira.driver),
            filtersPage = CloudFiltersPage(jira, jira.driver),
            createIssue = CreateAnIssue(
                jira = jira,
                meter = meter,
                projectMemory = similarities.projectMemory,
                createIssueButton = By.id("createGlobalItem")
            ),
            searchWithJql = SearchCloudFilter(
                jira = jira,
                meter = meter,
                filters = similarities.filtersMemory
            ),
            browseProjects = BrowseCloudProjects(
                jira = jira,
                meter = meter,
                projectMemory = similarities.projectMemory
            ),
            browseBoards = BrowseCloudBoards(
                jira = jira,
                meter = meter,
                boardsMemory = boardPages
            ),
            viewBoard = ViewCloudBoard(
                jira = jira,
                meter = meter,
                boardMemory = boardPages,
                issueKeyMemory = similarities.issueKeyMemory
            )
        )
    }

    class SkippingActionMetricOutput(
        private val skipProbability: Int,
        private val delegate: ActionMetricOutput
    ) : ActionMetricOutput {
        private val random = Random()

        override fun write(metric: ActionMetric) {
            if (random.nextInt(101) > skipProbability) {
                delegate.write(metric)
            }
        }
    }
}
