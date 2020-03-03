package jces1209

import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import com.atlassian.performance.tools.virtualusers.api.VirtualUserLoad
import com.atlassian.performance.tools.virtualusers.api.browsers.HeadlessChromeBrowser
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserBehavior
import jces1209.vu.EagerChromeBrowser
import java.time.Duration

class EagerSlowAndMeaningful : BenchmarkQuality {
    override fun provide(): VirtualUsersSource = AwsVus()

    override fun behave(scenario: Class<out Scenario>): VirtualUserBehavior = VirtualUserBehavior.Builder(scenario)
        .browser(EagerChromeBrowser::class.java)
        .load(
            VirtualUserLoad.Builder()
                .virtualUsers(72)
                .flat(Duration.ofMinutes(20))
                .build()
        )
        .skipSetup(true)
        .seed(12345L)
        .build()
}
