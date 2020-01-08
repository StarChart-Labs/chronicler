# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]

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
