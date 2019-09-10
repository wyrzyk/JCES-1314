package quick303.vu

import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import com.atlassian.performance.tools.jiraactions.api.page.wait
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.*
import java.time.Duration

class JiraCloudLogIn(
    private val userMemory: UserMemory,
    private val driver: WebDriver
) : Action {

    override fun run() {
        val user = userMemory
            .recall()
            ?: throw Exception("Don't remember which user I am")
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
}