package jces1209.vu.page.boards

import java.net.URI

interface BoardPage {

    val uri: URI
    fun waitForAnyIssue(): BoardContent
}
