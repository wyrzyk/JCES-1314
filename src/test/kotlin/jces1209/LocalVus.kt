package jces1209

import com.atlassian.performance.tools.aws.api.UnallocatedResource
import com.atlassian.performance.tools.awsinfrastructure.api.virtualusers.ProvisionedVirtualUsers
import com.atlassian.performance.tools.infrastructure.api.virtualusers.LocalVirtualUsers
import com.atlassian.performance.tools.infrastructure.api.virtualusers.VirtualUsers
import com.atlassian.performance.tools.virtualusers.api.VirtualUserOptions
import io.github.bonigarcia.wdm.FirefoxDriverManager
import java.nio.file.Path

class LocalVus : VirtualUsersSource {

    override fun obtainVus(
        resultsTarget: Path,
        workspace: Path
    ): ProvisionedVirtualUsers<*> {
        FirefoxDriverManager.getInstance().version("0.26.0").setup()
        return ProvisionedVirtualUsers(
            virtualUsers = SequentialVirtualUsers(LocalVirtualUsers(resultsTarget)),
            resource = UnallocatedResource()
        )
    }
}

/**
 * Works around https://ecosystem.atlassian.net/browse/JPERF-574
 */
private class SequentialVirtualUsers(
    private val base: VirtualUsers
) : VirtualUsers {


    override fun applyLoad(options: VirtualUserOptions) {
        synchronized(LOCK) {
            base.applyLoad(options)
        }
    }

    override fun gatherResults() {
        synchronized(LOCK) {
            base.gatherResults()
        }
    }

    private companion object {
        private val LOCK = Object()
    }
}
