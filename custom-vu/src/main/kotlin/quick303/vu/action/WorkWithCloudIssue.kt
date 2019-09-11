package quick303.vu.action

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.VIEW_ISSUE
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import quick303.vu.page.CloudIssuePage

class WorkWithCloudIssue(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val issueKeyMemory: IssueKeyMemory,
    private val random: SeededRandom,
    private val editProbability: Float,
    private val commentProbability: Float
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        val issueKey = issueKeyMemory.recall()
        if (issueKey == null) {
            logger.debug("I don't recall any issue keys. Maybe next time I will.")
            return
        }
        val issuePage = read(issueKey)
        if (random.random.nextFloat() < editProbability) {
            edit(issuePage)
        }
        if (random.random.nextFloat() < commentProbability) {
            comment(issuePage)
        }
    }

    private fun read(
        issueKey: String
    ): CloudIssuePage = meter.measure(VIEW_ISSUE) {
        jira.goToIssue(issueKey)
        CloudIssuePage(jira.driver).waitForSummary()
    }

    private fun edit(issuePage: CloudIssuePage) {
        logger.debug("I want to edit the $issuePage")
    }

    private fun comment(issuePage: CloudIssuePage) {
        logger.debug("I want to comment on the $issuePage")
    }
}