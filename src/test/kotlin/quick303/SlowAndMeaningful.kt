package quick303

import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import com.atlassian.performance.tools.virtualusers.api.VirtualUserLoad
import com.atlassian.performance.tools.virtualusers.api.browsers.HeadlessChromeBrowser
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserBehavior
import java.time.Duration

class SlowAndMeaningful : BenchmarkQuality {
    override fun provide(): VirtualUsersSource = AwsVus()

    override fun behave(scenario: Class<out Scenario>): VirtualUserBehavior = VirtualUserBehavior.Builder(scenario)
        .browser(HeadlessChromeBrowser::class.java)
        .load(
            VirtualUserLoad.Builder()
                .flat(Duration.ofMinutes(20))
                .build()
        )
        .skipSetup(true)
        .build()
}