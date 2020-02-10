package jces1209.vu.page.boards

import org.openqa.selenium.WebDriver
import java.net.URI

class ScrumBoardPage(
    private val driver: WebDriver,
    override val uri: URI
) : BoardPage by ClassicBoardPage(driver, uri)
