package jces1209.vu.page

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

class CloudIssuePage(
    private val driver: WebDriver
) : AbstractIssuePage {
    private val bentoSummary = By.cssSelector("[data-test-id='issue.views.issue-base.foundation.summary.heading']")
    private val classicSummary = By.id("key-val")
    private val falliblePage = FalliblePage.Builder(
        expectedContent = listOf(bentoSummary, classicSummary),
        webDriver = driver
    )
        .cloudErrors()
        .build()

    override fun waitForSummary(): AbstractIssuePage {
        falliblePage.waitForPageToLoad()
        return this
    }

    override fun comment(): Commenting {
        return if (isCommentingClassic()) {
            ClassicCloudCommenting(driver)
        } else {
            BentoCommenting(driver)
        }
    }

    private fun isCommentingClassic(): Boolean = driver
        .findElements(By.id("footer-comment-button"))
        .isNotEmpty()
}
