package quick303

import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserBehavior

interface BenchmarkQuality {

    fun provide(): VirtualUsersSource

    fun behave(
        scenario: Class<out Scenario>
    ): VirtualUserBehavior
}
