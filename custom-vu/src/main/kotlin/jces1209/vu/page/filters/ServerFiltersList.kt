package jces1209.vu.page.filters

import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import java.net.URI

class ServerFiltersList(
    private val element: WebElement
) : FiltersList {

    override fun listFilters(): List<URI> {
        val columnNames = element
            .findElements(By.tagName("th"))
            .map { it.text.trim() }
        val nameColumnIndex = columnNames.indexOf("Name")
        return element
            .findElements(By.cssSelector("tbody tr"))
            .map { row -> row.findElements(By.tagName("td")) }
            .map { cells ->
                val nameCell = cells[nameColumnIndex]!!
                val nameElement = nameCell.findElement(By.className("favourite-item"))
                findLink(nameElement)
            }
    }

    private fun findLink(
        element: WebElement
    ): URI = element
        .findElement(By.tagName("a"))
        .getAttribute("href")
        .let { URI(it) }
}
