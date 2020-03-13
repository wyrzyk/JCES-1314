## Compare Jira performance

Benchmark two Jiras against each other. Both [Jira Cloud] and [Jira Data Center] are supported.

1. Prepare [AWS credentials].
2. Point to the baseline instance via `jira-baseline.properties` file akin to the [example properties].
3. Point to the experiment instance via `jira-experiment.properties` file akin to the [example properties].
4. Run `./gradlew comparePerformance`.

    Read various reports logged in the console. The most useful ones are links to files,
    like `distribution-comparision.html` or `summary-per-cohort.html`.

[Jira Cloud]: docs/SET-UP-CLOUD.md
[Jira Data Center]: docs/PROVISION-DC.md
[AWS credentials]: docs/AWS-CREDENTIALS.md
[example properties]: example-jira.properties

### Prior art
Originally forked from [quick-303](https://github.com/atlassian/quick-303).
