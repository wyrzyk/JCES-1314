package jces1209.vu.page.filters

interface FiltersPage {

    fun open() :FiltersPage
    fun waitForList(): FiltersList
}
