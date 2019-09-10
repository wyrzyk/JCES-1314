import com.amazonaws.auth.AWSCredentialsProviderChain
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.auth.EC2ContainerCredentialsProviderWrapper
import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.atlassian.performance.tools.aws.api.Aws
import com.atlassian.performance.tools.aws.api.DependentResources
import com.atlassian.performance.tools.aws.api.Investment
import com.atlassian.performance.tools.aws.api.SshKeyFormula
import com.atlassian.performance.tools.awsinfrastructure.api.network.NetworkFormula
import com.atlassian.performance.tools.awsinfrastructure.api.virtualusers.MulticastVirtualUsersFormula
import com.atlassian.performance.tools.awsinfrastructure.api.virtualusers.ProvisionedVirtualUsers
import com.atlassian.performance.tools.infrastructure.api.virtualusers.DirectResultsTransport
import com.atlassian.performance.tools.io.api.dereference
import com.atlassian.performance.tools.jirasoftwareactions.api.JiraSoftwareScenario
import com.atlassian.performance.tools.report.api.FullReport
import com.atlassian.performance.tools.report.api.FullTimeline
import com.atlassian.performance.tools.report.api.result.RawCohortResult
import com.atlassian.performance.tools.virtualusers.api.VirtualUserOptions
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserBehavior
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserTarget
import com.atlassian.performance.tools.workspace.api.RootWorkspace
import org.junit.Test
import java.io.File
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture

class JiraOfferingComparisonIT {

    private val workspace = RootWorkspace(Paths.get("build")).currentTask

    @Test
    fun shouldCompareCloudWithDc() {
        val cloudResult = benchmark(
            cohort = "Cloud",
            options = VirtualUserOptions(
                target = loadCloudTarget(),
                behavior = VirtualUserBehavior.Builder(JiraSoftwareScenario::class.java).build()
            )
        )
        FullReport().dump(
            results = listOf(cloudResult).map { it.prepareForJudgement(FullTimeline()) },
            workspace = workspace.isolateTest("Compare")
        )
    }

    private fun loadCloudTarget(): VirtualUserTarget {
        val jiraCloud = Properties()
        File("jira-cloud.properties").bufferedReader().use { jiraCloud.load(it) }
        return VirtualUserTarget(
            webApplication = URI(jiraCloud.getProperty("jira.uri")!!),
            userName = jiraCloud.getProperty("user.name")!!,
            password = jiraCloud.getProperty("user.password")!!
        )
    }

    private fun benchmark(
        cohort: String,
        options: VirtualUserOptions
    ): RawCohortResult {
        val resultsTarget = workspace.directory.resolve("vu-results").resolve(cohort)
        val (virtualUsers, resource) = prepareVirtualUsers(resultsTarget)
        return try {
            virtualUsers.applyLoad(options)
            virtualUsers.gatherResults()
            RawCohortResult.Factory().fullResult(cohort, resultsTarget)
        } catch (e: Exception) {
            virtualUsers.gatherResults()
            RawCohortResult.Factory().failedResult(cohort, resultsTarget, e)
        } finally {
            resource.release().get()
        }
    }

    private fun prepareVirtualUsers(
        resultsTarget: Path
    ): ProvisionedVirtualUsers<*> {
        val aws = prepareAws()
        val nonce = UUID.randomUUID().toString()
        val sshKey = SshKeyFormula(
            ec2 = aws.ec2,
            workingDirectory = workspace.directory,
            prefix = nonce,
            lifespan = Duration.ofMinutes(30)
        ).provision()
        val investment = Investment(
            useCase = "Compare Jira Cloud vs DC",
            lifespan = Duration.ofMinutes(30)
        )
        val network = NetworkFormula(
            investment = investment,
            aws = aws
        ).provision()
        val (virtualUsers, vuResource) = MulticastVirtualUsersFormula.Builder(
            nodes = 6,
            shadowJar = dereference("jpt.virtual-users.shadow-jar")
        )
            .network(network)
            .build()
            .provision(
                investment = investment,
                shadowJarTransport = aws.virtualUsersStorage(nonce),
                resultsTransport = DirectResultsTransport(resultsTarget),
                roleProfile = aws.shortTermStorageAccess(),
                key = CompletableFuture.completedFuture(sshKey),
                aws = aws
            )
        return ProvisionedVirtualUsers(
            virtualUsers = virtualUsers,
            resource = DependentResources(
                user = vuResource,
                dependency = sshKey.remote
            )
        )
    }

    private fun prepareAws() = Aws.Builder(Regions.EU_WEST_1)
        .credentialsProvider(
            AWSCredentialsProviderChain(
                STSAssumeRoleSessionCredentialsProvider.Builder(
                    "arn:aws:iam::695067801333:role/server-gdn-bamboo",
                    UUID.randomUUID().toString()
                ).build(),
                ProfileCredentialsProvider("jpt-dev"),
                EC2ContainerCredentialsProviderWrapper(),
                DefaultAWSCredentialsProviderChain()
            )
        )
        .regionsWithHousekeeping(listOf(Regions.EU_WEST_1))
        .batchingCloudformationRefreshPeriod(Duration.ofSeconds(20))
        .build()

    private operator fun ProvisionedVirtualUsers<*>.component1() = this.virtualUsers
    private operator fun ProvisionedVirtualUsers<*>.component2() = this.resource
}