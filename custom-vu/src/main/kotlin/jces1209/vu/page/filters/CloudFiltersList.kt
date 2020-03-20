package jces1209.vu.page.filters

import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import java.net.URI

class CloudFiltersList(
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
                val name = cells[nameColumnIndex]!!
                name.findElement(By.tagName("a")).getAttribute("href").let { URI(it) }
            }
    }
}
