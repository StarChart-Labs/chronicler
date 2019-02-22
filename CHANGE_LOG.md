# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]
## Changed
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
