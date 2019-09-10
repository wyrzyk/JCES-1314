package quick303

import com.atlassian.performance.tools.awsinfrastructure.api.virtualusers.ProvisionedVirtualUsers
import java.nio.file.Path

interface VirtualUsersSource {

    fun obtainVus(
        resultsTarget: Path,
        workspace: Path
    ): ProvisionedVirtualUsers<*>
}
