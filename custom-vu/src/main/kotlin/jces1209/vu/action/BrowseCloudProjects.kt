package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.BROWSE_PROJECTS
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.ProjectMemory
import jces1209.vu.page.JiraCloudProjectList
import java.time.Duration

class BrowseCloudProjects(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val projectMemory: ProjectMemory
) : Action {

    override fun run() {
        val projectList = meter.measure(BROWSE_PROJECTS) {
            jira.navigateTo("projects")
            JiraCloudProjectList(jira.driver).lookForProjects()
        }
        projectMemory.remember(projectList.listProjects())
    }
}
