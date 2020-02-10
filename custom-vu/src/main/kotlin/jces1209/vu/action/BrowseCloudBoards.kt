package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.BROWSE_BOARDS
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.Memory
import jces1209.vu.page.CloudBrowseBoardsPage
import jces1209.vu.page.boards.BoardPage
import net.jcip.annotations.NotThreadSafe

@NotThreadSafe
class BrowseCloudBoards(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val boardsMemory: Memory<BoardPage>
) : Action {

    override fun run() {
        val boardList = meter.measure(BROWSE_BOARDS) {
            jira.navigateTo("secure/ManageRapidViews.jspa")
            CloudBrowseBoardsPage(jira.driver).waitForBoards()
        }
        boardsMemory.remember(boardList.listBoards())
    }
}
