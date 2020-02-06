package jces1209.vu.page

import com.atlassian.performance.tools.jirasoftwareactions.api.boards.AgileBoard
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import java.net.URI

class CloudBoardList(
    private val element: WebElement
) {
    fun listBoards(): List<AgileBoard> {
        return element
            .findElements(By.tagName("a"))
            .asSequence()
            .map { it.getAttribute("href") }
            .map { URI(it).path }
            .filter { it.contains("projects") }
            .filter { it.contains("boards") }
            .map { it.split('/').last() }
            .map { AgileBoard(it) }
            .toList()
    }
}
