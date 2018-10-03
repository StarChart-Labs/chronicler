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

## Running Locally

TODO Needs updating for AWS!

If you wish to run the application yourself, you will have to setup a GitHub App. 

First, you will need a location for webhook events to be sent. Currently, the best solution we've found for this is [RequestBin](https://requestb.in). Note that it expires every 48 hours.

Next, create an App called "Chronicler (Development Testing)" that can only be installed on your account, matching the settings described in [GitHub App Settings](./doc/github-app-settings.md), with a webhook secret of "chronicler" (the secret matches one encrypted in test files). Finally, run `./gradlew run` locally. Repositories you install your test GitHub App on will send requests to RequestBin, which you can then take and send on to you local server via a tool such as Postman.
