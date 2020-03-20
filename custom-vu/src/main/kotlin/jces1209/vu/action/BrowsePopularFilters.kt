package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.ActionType
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.Memory
import jces1209.vu.page.filters.FiltersPage
import java.net.URI

class BrowsePopularFilters(
    private val filtersPage: FiltersPage,
    private val meter: ActionMeter,
    private val filters: Memory<URI>
) : Action {

    private val actionType = ActionType<Any>("Browse Popular Filters") {}

    override fun run() {
        val filtersList = meter.measure(actionType) {
            filtersPage.open().waitForList()
        }
        filtersList
            .listFilters()
            .take(16)
            .let { filters.remember(it) }
    }
}
