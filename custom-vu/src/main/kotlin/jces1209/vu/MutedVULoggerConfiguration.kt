package jces1209.vu

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.DefaultConfiguration

class MutedVULoggerConfiguration: DefaultConfiguration() {

    override fun doConfigure() {
        super.doConfigure()
        this.getLoggerConfig("com.atlassian.performance.tools.virtualusers.ExploratoryVirtualUser").level = Level.OFF
    }
}
