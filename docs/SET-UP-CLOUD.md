# Set up Jira Cloud

1. Invite the Jira user.
2. Apply the workaround from [JPERF-379]: remove the activity stream gadget from the system dashboard.
3. Ensure there's at least one project and one board.
4. Use [`JiraCloudScenario`] in the [benchmark].

Jira Cloud free trial can expire. In such case, reprovision it and update the property files if necessary.

[JPERF-379]: https://ecosystem.atlassian.net/browse/JPERF-379
[`JiraCloudScenario`]: ../custom-vu/src/main/kotlin/jces1209/vu/JiraCloudScenario.kt
[benchmark]: ../src/test/kotlin/JiraPerformanceComparisonIT.kt
