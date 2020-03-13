# Providing AWS credentials

You'll need AWS credentials, which have [full JPT permissions].
The order of loading credentials is defined in [`AwsVus`] in the `prepareAws` method.

[full JPT permissions]: https://bitbucket.org/atlassian/jira-performance-tests/src/master/aws-policy.json
[`AwsVus`]: ../src/test/kotlin/jces1209/AwsVus.kt
