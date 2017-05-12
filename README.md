# Chronicler

[![Travis CI](https://img.shields.io/travis/Corona-IDE/chronicler.svg?branch=master)](https://travis-ci.org/Corona-IDE/chronicler) [![Black Duck Security Risk](https://copilot.blackducksoftware.com/github/groups/Corona-IDE/locations/chronicler/public/results/branches/master/badge-risk.svg)](https://copilot.blackducksoftware.com/github/groups/Corona-IDE/locations/chronicler/public/results/branches/master)

GitHub integration which validates release note/change log updates occur

Features in progress:

- Specify groups of files which coorespond to a release note file which should be updated when they change (including ability to exclude files patterns from that group)
- Specify if the check MUST pass (required) for a PR to be merged (Travis is "required", CodeCov is not)
- Allowing collaborators (push access) to override status check failure with a comment mentioning the integration
