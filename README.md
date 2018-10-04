# Chronicler

[![Travis CI](https://img.shields.io/travis/StarChart-Labs/chronicler.svg?branch=master)](https://travis-ci.org/StarChart-Labs/chronicler) [![Black Duck Security Risk](https://copilot.blackducksoftware.com/github/repos/StarChart-Labs/chronicler/branches/master/badge-risk.svg)](https://copilot.blackducksoftware.com/github/repos/StarChart-Labs/chronicler/branches/master)

GitHub integration which validates whether release notes are updated when production code is changed

## Contributing

Information for how to contribute to Chronicler can be found in [the contribution guidelines](./CONTRIBUTING.md)

## Legal

Chronicler is distributed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0). The only requirement for use in inclusion of the following line within your NOTICES file:

```
StarChart-Labs Chronicler Web Application
Copyright 2017 StarChart Labs Authors.

This product includes software developed at
StarChart Labs (http://www.starchartlabs.org/).
```

The requirement for a copy of the license being included in distributions is fulfilled by a copy of the [LICENSE](./LICENSE) file being included in constructed JAR archives

## Reporting Vulnerabilities

If you discover a security vulnerability, contact the development team by e-mail at corona.ide.dev@gmail.com

## Serverless Conversion Notes

- Note: After first deploy, need to go to API Gateway, get Target Domain Name from Custom Domain page, and setup CNAME record in Google Domains
- Should I figure out how to use cloud formation to create serverless group/users that can be limited based both on stage and on service? "policies"?
  - Allow setup per stage of user, automate integration as profile into serverless environment (still requires user with at least create user/policy setup)

### Not Yet Automated

- Deployment/Rotation of GitHub credentials used by functions?
- CName update in google domains? (needs to be set when stack is created, but not for deploys after - cloudfront stays static)

### TODO

NOTE: TO run serverless deployment, you must npm install a couple serverless plug-ins, as well as serverless itself:

```
npm install --save-dev serverless-iam-roles-per-function
npm install --save-dev serverless-aws-alias
```

- Further narrow serverless user permissions, different users for dev, valid (cd) and production
- service telemetry (newrelic-style info) on aws
- usage alerts
- add tags to resources to form a resource group
- reduce memory allocation of webhook handler
- SNS topic names into AWS System Manager Parameters (read info env var, cloudformation update/set as part of serverless deploy)
   - https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-ssm-parameter.html

### Serverless Plugins?

- standards plugins: defines standardized naming conventions for things like parameter store keys, sns topics, etc

### Gradle integration

- Serverless should trigger build prior to deploy
- Gradle build should perhaps create a yaml that serverless can consume with built artifact locations
- Maybe combo of serverless plug-in and gradle plug-in that builds and then loads the file?

### GitHub integration

- Serverless/GitHub tool that sets up an app, and then grabs it's webhook secret and PEM and loads into AWS SSM Parameter Store?