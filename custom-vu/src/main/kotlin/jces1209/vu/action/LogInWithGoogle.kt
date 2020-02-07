package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.LOG_IN
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.User
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import jces1209.vu.page.JiraCloudWelcome
import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable
import org.openqa.selenium.support.ui.ExpectedConditions.or

class LogInWithGoogle(
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
        JiraCloudWelcome(jira.driver).skipToJira()
    }

    private fun logIn(
        driver: WebDriver,
        user: User
    ) {
        jira.goToLogin()
        driver
            .wait(elementToBeClickable(By.id("google-signin-button")))
            .also { it.click() }
        val challengeLocator = By.id("identifierId")
        val reLogLocator = By.cssSelector("[data-email='${user.name}']")
        driver.wait(
            or(
                elementToBeClickable(challengeLocator),
                elementToBeClickable(reLogLocator)
            )
        )
        val challenge = driver.findElements(challengeLocator).singleOrNull()
        if (challenge != null) {
            answerChallenge(challenge, user, driver)
        } else {
            driver.findElement(reLogLocator).click()
        }
        driver.wait(ExpectedConditions.presenceOfElementLocated(By.id("jira")))
    }

    private fun answerChallenge(nameInput: WebElement, user: User, driver: WebDriver) {
        nameInput
            .also { it.sendKeys(user.name) }
            .also { it.sendKeys(Keys.RETURN) }
        driver
            .wait(elementToBeClickable(By.cssSelector("[type=password]")))
            .also { it.sendKeys(user.password) }
            .also { it.sendKeys(Keys.RETURN) }
    }
}
