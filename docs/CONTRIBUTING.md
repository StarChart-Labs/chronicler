# Contributing

We welcome any contributions! If you wish to contribute:

- Fork the `chronicler` repository
- Clone your fork to your development machine
- Run `./gradlew clean build` to confirm you are starting from a working Setup
 - Please report any issues with this build step in the GitHub project's issues
- Create a branch for your work
- Setup your development environment (see below)
- Make changes
- Run `./gradlew clean build` to test your changes locally
- Push your branch to your Fork
- Make a Pull Request against the `master` branch

## Development Environment Setup

Currently, Eclipse is the supported IDE for development of Chronicler. It is recommended to create an isolated workspace for StarChart Labs projects. You should also import the standard StarChart Labs formatting and save settings from the [eclipse-configuration repository](https://github.com/StarChart-Labs/eclipse-configuration)

## General Standards

In general, pull requests should:
- Be small and focused on a single improvement/bug
- Include tests for changed behavior/new features
- Match the formatting of the existing code
- Have documentation for added methods/classes and appropriate in-line comments
- Have additions to the CHANGE_LOG.md file recording changed behavior

## Running Serverless Locally

To run a serverless deployment, you must npm install a couple serverless plug-ins, as well as serverless itself:

```
npm install serverless
npm install --save-dev serverless-iam-roles-per-function
npm install --save-dev serverless-aws-alias
npm install --save-dev serverless-iam-roles-per-function
npm install --save-dev serverless-domain-manager
npm install --save-dev serverless-s3-deploy
npm install --save-dev serverless-apigw-binary
npm install --save-dev serverless-pseudo-parameters
```