package jces1209.vu.page

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions

class JiraCloudWelcome(
    private val driver: WebDriver
) {

    fun skipToJira() = apply {
        driver.wait(ExpectedConditions.presenceOfElementLocated(By.id("jira")))
        repeat(2) {
            skipQuestion()
        }
    }

    private fun skipQuestion() {
        driver
            .findElements(By.xpath("//*[contains(text(), 'Skip question')]"))
            .forEach { it.click() }
    }
}
