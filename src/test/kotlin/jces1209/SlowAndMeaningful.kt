package jces1209

import com.amazonaws.regions.Regions
import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import com.atlassian.performance.tools.virtualusers.api.TemporalRate
import com.atlassian.performance.tools.virtualusers.api.VirtualUserLoad
import com.atlassian.performance.tools.virtualusers.api.browsers.Browser
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserBehavior
import jces1209.vu.EagerChromeBrowser
import jces1209.vu.MutedVULoggerConfiguration
import java.time.Duration

class SlowAndMeaningful private constructor(
    private val browser: Class<out Browser>,
    private val region: Regions,
    private val ramp: Duration,
    private val flat: Duration
) : BenchmarkQuality {

    override fun provide(): VirtualUsersSource = AwsVus(ramp + flat + Duration.ofHours(3), region)

    override fun behave(scenario: Class<out Scenario>): VirtualUserBehavior = VirtualUserBehavior.Builder(scenario)
        .browser(browser)
        .load(
            VirtualUserLoad.Builder()
                .virtualUsers(72)
                .maxOverallLoad(TemporalRate(15.0, Duration.ofSeconds(1)))
                .ramp(ramp)
                .flat(flat)
                .build()
        )
        .skipSetup(true)
        .seed(12345L)
        .logging(MutedVULoggerConfiguration::class.java)
        .build()

    class Builder {
        private var browser: Class<out Browser> = EagerChromeBrowser::class.java
        private var region: Regions = Regions.US_EAST_1
        private var ramp: Duration = Duration.ofMinutes(20)
        private var flat: Duration = Duration.ofMinutes(20)

        fun browser(browser: Class<out Browser>) = apply { this.browser = browser }
        fun region(region: Regions) = apply { this.region = region }
        fun ramp(ramp: Duration) = apply { this.ramp = ramp }
        fun flat(flat: Duration) = apply { this.flat = flat }

        fun build(): BenchmarkQuality {
            return SlowAndMeaningful(
                browser,
                region,
                ramp,
                flat
            )
        }
    }
}
