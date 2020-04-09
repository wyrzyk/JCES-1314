package jces1209.vu.page

import com.atlassian.performance.tools.jiraactions.api.memories.Project
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

class JiraCloudProjectList(
    private val driver: WebDriver
) {
    private val projectListSelector = By.cssSelector(
        "[data-test-id='global-pages.directories.directory-base.content.table.container']"
    )
    private val falliblePage = FalliblePage.Builder(
        expectedContent = listOf(projectListSelector),
        webDriver = driver
    )
        .cloudErrors()
        .build()

    fun lookForProjects(): JiraCloudProjectList {
        falliblePage.waitForPageToLoad()
        return this
    }

    fun listProjects(): List<Project> {
        return driver
            .findElement(projectListSelector)
            .findElement(By.tagName("tbody"))
            .findElements(By.tagName("tr"))
            .map { extractProject(it) }
    }

    private fun extractProject(
        row: WebElement
    ): Project {
        val cells = row.findElements(By.tagName("td"))
        return Project(
            key = cells.elementAtOrNull(2)?.text?.trim() ?: throw Exception("There's no third cell"),
            name = cells.elementAtOrNull(1)?.text?.trim() ?: throw Exception("There's no second cell")
        )
    }
}
