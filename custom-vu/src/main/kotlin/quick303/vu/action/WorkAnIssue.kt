package quick303.vu.action

import com.atlassian.performance.tools.jiraactions.api.ADD_COMMENT
import com.atlassian.performance.tools.jiraactions.api.ADD_COMMENT_SUBMIT
import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.VIEW_ISSUE
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import quick303.vu.page.CloudIssuePage

class WorkAnIssue(
    private val issuePage: CloudIssuePage,
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val issueKeyMemory: IssueKeyMemory,
    private val random: SeededRandom,
    private val commentProbability: Float
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        val issueKey = issueKeyMemory.recall()
        if (issueKey == null) {
            logger.debug("I don't recall any issue keys. Maybe next time I will.")
            return
        }
        val loadedIssuePage = read(issueKey)
        if (random.random.nextFloat() < commentProbability) {
            comment(loadedIssuePage)
        }
    }

    private fun read(
        issueKey: String
    ) = meter.measure(VIEW_ISSUE) {
        jira.goToIssue(issueKey)
        issuePage.waitForSummary()
    }

    private fun comment(issuePage: CloudIssuePage) {
        val commenting = issuePage.comment()
        meter.measure(ADD_COMMENT) {
            commenting.openEditor()
            commenting.typeIn("abc def")
            meter.measure(ADD_COMMENT_SUBMIT) {
                commenting.saveComment()
                commenting.waitForTheNewComment()
            }
        }
    }
}
