package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.LOG_IN
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.User
import com.atlassian.performance.tools.jiraactions.api.page.wait
import jces1209.vu.page.JiraCloudWelcome
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated
import java.time.Duration

/**
 * Logs in via Atlassian ID.
 *
 * If the [user] signed up with Google, this won't work.
 * In those cases use [LogInWithGoogle], but be careful and read its docs.
 *
 * In order to avoid Google, use a different email provider, like [FastMail.com](fastmail.com)
 * or Atlassian [internal setup](http://go.atlassian.com/usersinbuckets). Invite the email via Jira user management GUI.
 * Finish the signup via the received email invite and it be able to log in.
 */
class LogInWithAtlassianId(
    private val user: User,
    private val jira: WebJira,
    private val meter: ActionMeter
) : Action {

    override fun run() {
        meter.measure(LOG_IN) {
            logIn()
        }
    }

    private fun logIn() {
        val driver = jira.driver
        jira.goToLogin()
        driver
            .wait(
                condition = elementToBeClickable(By.id("username")),
                timeout = Duration.ofSeconds(15)
            )
            .also { it.sendKeys(user.name) }
            .also { it.sendKeys(Keys.RETURN) }
        driver
            .wait(
                condition = presenceOfElementLocated(By.id("password")),
                timeout = Duration.ofSeconds(5)
            )
            .also {
                driver.wait(
                    condition = elementToBeClickable(it),
                    timeout = Duration.ofSeconds(5)
                )
            }
            .also { it.sendKeys(user.password) }
            .also { it.sendKeys(Keys.RETURN) }
        JiraCloudWelcome(jira.driver).skipToJira()
    }
}
