package quick303.vu.page

import com.atlassian.performance.tools.jiraactions.api.page.JiraErrors
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions.or
import org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated
import quick303.vu.wait

class DcIssuePage(
    private val driver: WebDriver
) : AbstractIssuePage {

    override fun waitForSummary(): DcIssuePage {
        val jiraErrors = JiraErrors(driver)
        driver.wait(
            or(
                visibilityOfElementLocated(By.id("key-val")),
                jiraErrors.anyCommonError()
            )
        )
        jiraErrors.assertNoErrors()
        return this
    }

    override fun comment(): Commenting {
        return DcCommenting(driver)
    }
}