package jces1209.vu.page

import jces1209.vu.page.boards.BoardPage
import jces1209.vu.page.boards.KanbanBoardPage
import jces1209.vu.page.boards.NextGenBoardPage
import jces1209.vu.page.boards.ScrumBoardPage
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import java.net.URI

class CloudBoardList(
    private val element: WebElement,
    private val driver: WebDriver
) {
    fun listBoards(): List<BoardPage> {
        val columnNames = element
            .findElements(By.tagName("th"))
            .map { it.text.trim() }
        val nameColumnIndex = columnNames.indexOf("Name")
        val typeColumnIndex = columnNames.indexOf("Type")
        return element
            .findElements(By.cssSelector("tbody tr"))
            .map { row -> row.findElements(By.tagName("td")) }
            .map { cells ->
                val type = cells[typeColumnIndex]!!.text.trim()
                val name = cells[nameColumnIndex]!!
                val uri = name.findElement(By.tagName("a")).getAttribute("href").let { URI(it) }
                when (type) {
                    "Kanban" -> KanbanBoardPage(driver, uri)
                    "Scrum" -> ScrumBoardPage(driver, uri)
                    "Next-gen" -> NextGenBoardPage(driver, uri)
                    else -> throw Exception("Unknown board type: $type")
                }
            }
    }
}
