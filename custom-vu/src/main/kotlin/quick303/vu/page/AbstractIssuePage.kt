package quick303.vu.page

interface AbstractIssuePage {

    fun waitForSummary(): AbstractIssuePage
    fun comment(): Commenting
}