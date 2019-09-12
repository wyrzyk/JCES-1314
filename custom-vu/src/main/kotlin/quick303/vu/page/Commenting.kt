package quick303.vu.page

interface Commenting {

    fun openEditor()
    fun typeIn(comment: String)
    fun saveComment()
    fun waitForTheNewComment()
}
