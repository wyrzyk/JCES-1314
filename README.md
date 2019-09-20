## Compare Jira offerings

Benchmark Jira Cloud vs Jira DC.

1. Prepare AWS credentials in any standard way.
2. Point to Jira Cloud via `jira-cloud.properties` file akin to the [example properties].

    Invite the Jira user.
    The benchmark needs to work around [JPERF-379].
    To do that, remove the activity stream gadget from the system dashboard.

3. Point to Jira DC via `jira-dc.properties` file akin to the [example properties].

    You can provision DC via [internal provisioning code]. Note the instruction in the commit message.

4. Run `./gradlew compareOfferings`.

    Read various reports logged in the console. The most useful ones are links to files,
    like `distribution-comparision.html` or `summary-per-cohort.html`.

[example properties]: example-jira.properties
[JPERF-379]: https://ecosystem.atlassian.net/browse/JPERF-379
[internal provisioning code]: https://stash.atlassian.com/projects/JIRASERVER/repos/jira-performance-tests/commits/b5da5e7fe64b83d4ec1f4d27fc178eae7f38b75d
