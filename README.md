## Compare Jira performance

Benchmark two Jiras against each other.

1. Prepare AWS credentials in any standard way.
2. Point to the baseline instance via `jira-baseline.properties` file akin to the [example properties].

    Invite the Jira user.
    The benchmark needs to work around [JPERF-379].
    To do that, remove the activity stream gadget from the system dashboard.
    The benchmark expects at least one project and one board to exist.

3. Point to the experiment instance via `jira-experiment.properties` file akin to the [example properties].

It's the same process as in the previous step.

4. Run `./gradlew comparePerformance`.

    Read various reports logged in the console. The most useful ones are links to files,
    like `distribution-comparision.html` or `summary-per-cohort.html`.
    
    
Note that Jira Cloud free trial can expire.
In such case, reprovision it and update the pointers.

[example properties]: example-jira.properties
[JPERF-379]: https://ecosystem.atlassian.net/browse/JPERF-379

### Prior art
Originally forked from [quick-303](https://github.com/atlassian/quick-303).
