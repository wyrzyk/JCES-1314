package jces1209.vu.page.boards

interface BoardContent {

    fun getIssueCount(): Int
    fun getIssueKeys(): Collection<String>
}
