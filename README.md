# Chronicler

[![Travis CI](https://img.shields.io/travis/StarChart-Labs/chronicler.svg?branch=master)](https://travis-ci.org/StarChart-Labs/chronicler) [![Black Duck Security Risk](https://copilot.blackducksoftware.com/github/repos/StarChart-Labs/chronicler/branches/master/badge-risk.svg)](https://copilot.blackducksoftware.com/github/repos/StarChart-Labs/chronicler/branches/master)

GitHub integration which validates release note/change log updates occur

Features in progress:

- Specify groups of files which coorespond to a release note file which should be updated when they change (including ability to exclude files patterns from that group)
- Specify if the check MUST pass (required) for a PR to be merged (Travis is "required", CodeCov is not)
- Allowing collaborators (push access) to override status check failure with a comment mentioning the integration

## Legal

Chronicler is distributed under the [MIT License](https://opensource.org/licenses/MIT). There are no requirements for using it in your own project (a line in a NOTICES file is appreciated but not necessary for use)

The requirement for a copy of the license being included in distributions is fulfilled by a copy of the [LICENSE](./LICENSE) file being included in constructed JAR archives
