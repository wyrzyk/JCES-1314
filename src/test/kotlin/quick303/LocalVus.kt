package quick303

import com.atlassian.performance.tools.aws.api.UnallocatedResource
import com.atlassian.performance.tools.awsinfrastructure.api.virtualusers.ProvisionedVirtualUsers
import com.atlassian.performance.tools.infrastructure.api.virtualusers.LocalVirtualUsers
import java.nio.file.Path

class LocalVus : VirtualUsersSource {

    override fun obtainVus(
        resultsTarget: Path,
        workspace: Path
    ): ProvisionedVirtualUsers<*> = ProvisionedVirtualUsers(
        virtualUsers = LocalVirtualUsers(resultsTarget),
        resource = UnallocatedResource()
    )
}