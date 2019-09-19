package quick303.vu.action

import com.atlassian.performance.tools.jiraactions.api.LOG_IN
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.User
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import com.atlassian.performance.tools.jiraactions.api.page.wait
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable
import org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated
import java.time.Duration

class JiraCloudLogIn(
    private val userMemory: UserMemory,
    private val jira: WebJira,
    private val meter: ActionMeter
) : Action {

    override fun run() {
        val user = userMemory
            .recall()
            ?: throw Exception("Don't remember which user I am")
        meter.measure(LOG_IN) {
            logIn(jira.driver, user)
        }
        repeat(2) {
            skipQuestion(jira.driver)
        }
    }

    private fun logIn(
        driver: WebDriver,
        user: User
    ) {
        driver.navigate().to(jira.base.toURL())
        driver
            .wait(
                condition = elementToBeClickable(By.id("username")),
                timeout = Duration.ofSeconds(15)
            )
            .also { it.sendKeys(user.name) }
            .also { it.sendKeys(Keys.RETURN) }
        driver
            .wait(
                condition = elementToBeClickable(By.id("password")),
                timeout = Duration.ofSeconds(20)
            )
            .also { it.sendKeys(user.password) }
            .also { it.sendKeys(Keys.RETURN) }
        driver.wait(
            condition = visibilityOfElementLocated(By.id("jira")),
            timeout = Duration.ofSeconds(30)
        )
    }

    private fun skipQuestion(driver: WebDriver) {
        driver
            .findElements(By.xpath("//*[contains(text(), 'Skip question')]"))
            .forEach { it.click() }
    }
}
