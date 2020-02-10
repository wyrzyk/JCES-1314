package jces1209

import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import com.atlassian.performance.tools.virtualusers.api.VirtualUserLoad
import com.atlassian.performance.tools.virtualusers.api.browsers.GoogleChrome
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserBehavior
import java.time.Duration

class QuickAndDirty : BenchmarkQuality {

    override fun provide(): VirtualUsersSource = LocalVus()

    override fun behave(
        scenario: Class<out Scenario>
    ): VirtualUserBehavior = VirtualUserBehavior.Builder(scenario)
        .browser(GoogleChrome::class.java)
        .load(
            VirtualUserLoad.Builder()
                .ramp(Duration.ZERO)
                .flat(Duration.ofMinutes(3))
                .virtualUsers(1)
                .build()
        )
        .skipSetup(true)
        .seed(12345L)
        .build()
}
