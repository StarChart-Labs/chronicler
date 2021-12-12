# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]
### Changed
- Updated serverless to 2.54.0
- Replaced serverless pseudo-parameters plug-in with native variable use
- Updated com.amazonaws:aws-java-sdk-cloudwatch from 1.11.699 to 1.12.128
- Updated com.amazonaws:aws-java-sdk-sns from 1.11.699 to 1.12.128
- Updated com.amazonaws:aws-java-sdk-ssm from 1.11.699 to 1.12.128
- Updated com.amazonaws:aws-lambda-java-core from 1.2.0 to 1.2.1
- Updated com.amazonaws:aws-lambda-java-events from 2.2.7 to 3.11.0
- Updated com.amazonaws:aws-lambda-java-log4j2 from 1.1.0 to 1.3.0
- Updated com.fasterxml.jackson.core:jackson-databind from 2.10.1 to 2.13.0
- Updated com.google.code.gson:gson from 2.8.6 to 2.8.9
- Updated com.squareup.okhttp3:okhttp from 3.14.4 to 4.9.3
- Updated org.apache.logging.log4j:log4j-core from 2.13.0 to 2.15.0
- Updated org.apache.logging.log4j:log4j-slf4j-impl from 2.13.0 to 2.15.0
- Updated org.starchartlabs.alloy:alloy-core from 0.5.0 to 1.0.2
- Updated org.starchartlabs.calamari:calamari-core from 0.4.1 to 1.0.2
- Updated org.starchartlabs.machete:machete-sns from 0.2.2 to 1.0.2
- Updated org.starchartlabs.machete:machete-ssm from 0.2.2 to 1.0.2
- Updated org.starchartlabs.majortom:event-model from 0.2.2 to 1.0.1
- Removed unused org.mockito:mockito-core dependency
- Updated org.slf4j:slf4j-api from 1.7.29 to 1.7.32
- Updated org.slf4j:slf4j-simple from 1.7.29 to 1.7.32
- Updated org.testng:testng from 6.14.3 to 7.4.0
- Updated org.yaml:snakeyaml from 1.25 to 1.29

## [0.5.2]
### Changed
- Update AWS, Jackson, Starchart, logging, and test dependencies to latest bugfix release
- GH-136 Reduce logging for installation modifications

## [0.5.1]
### Changed
- (GH-125) Update install button to send users to marketplace page
- Updated AWS and jackson dependencies to latest bugfix versions
- Updated test frameworks to latest bugfix versions

## [0.5.0]
### Added
- Add notifications to StarChart-Labs channels for system status
- Updated metrics stored in AWS for usage data/feedback
- (GH-118) Added metrics for number of pull requests serviced

### Changed
- Updated jackson-databind to 2.9.9 to address security vulnerabilities

## [0.4.0]
### Added
- Added privacy-policy and terms HTML pages
- GH-107 Added handling for marketplace events

## [0.3.1]
### Changed
- GH-102 Fix file copyright headers to match the declared license of Apache 2.0
- GH-103 Update to Calamari 0.3.3 to address potential timing attack vulnerability

## [0.3.0]
### Changed
- Moved webhook event handling out of AWS handler class
- Updated external dependencies to latest micro/minor versions
- Overrode Jackson dependency version from AWS SDK to version not flagged with vulnerabilities
- Add badge for READMEs

## [0.2.3]
### Changed
- GH-92: Made the namespace of the metric for installations counts configurable per deployment

## [0.2.2]
### Changed
- Fixed URLs for installation button

## [0.2.1]
### Changed
- Replaced local webhook verifier with calamari-core implementation
- Refactored use pattern of configuration file reader
- Moved machete project to machete-additions to allow creation of common library without overlap
- Fixed GH-81, allowing the Chronicler pull request status check to persist after merge conflict resolution
- Switched to Alloy-based spliterator implementation for GitHub paging
- Replaced calamari-additions project with Calamari library calls
- Replaced machete-additions project with Machete library calls

## [0.2.0]
### Added
- Ability to configure what is considered a release note or production file
- Fix HTTP responses not being closed properly by library via update to Calamari 0.2.1

## [0.1.0]
### Added
- Analysis to determine if release notes were modified alongside production files
- Functionality to integrate with GitHub status system, passing/failing a pull request based on release documentation
