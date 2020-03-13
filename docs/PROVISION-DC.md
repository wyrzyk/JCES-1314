# Provision Data Center

To provision a large Data Center cluster, perhaps for a baseline:
1. Set up [AWS credentials].
2. Check out [internal provisioning code].
3. Run `./gradlew testManually` in the repo above.
4. Use [`JiraDcScenario`] in the [benchmark].

[AWS credentials]: AWS-CREDENTIALS.md
[internal provisioning code]: https://stash.atlassian.com/projects/JIRASERVER/repos/jira-performance-tests/commits/2c7c1e197f80168cfe952e366f8bd5b4fdd31e8c
[`JiraDcScenario`]: ../custom-vu/src/main/kotlin/jces1209/vu/JiraDcScenario.kt
[benchmark]: ../src/test/kotlin/JiraPerformanceComparisonIT.kt
