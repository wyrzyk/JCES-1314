package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.VIEW_BOARD
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.Memory
import com.atlassian.performance.tools.jiraactions.api.observation.IssuesOnBoard
import jces1209.vu.page.boards.BoardPage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ViewCloudBoard(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val boardMemory: Memory<BoardPage>,
    private val issueKeyMemory: IssueKeyMemory
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        val board = boardMemory.recall()
        if (board == null) {
            logger.debug("I cannot recall any board, skipping...")
            return
        }
        meter.measure(
            key = VIEW_BOARD,
            action = {
                jira.driver.navigate().to(board.uri.toURL())
                board.waitForBoardPageToLoad()
            },
            observation = { boardContent ->
                issueKeyMemory.remember(boardContent.getIssueKeys())
                IssuesOnBoard(boardContent.getIssueCount()).serialize()
            }
        )
    }
}
