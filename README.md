# Chronicler

[![Travis CI](https://img.shields.io/travis/StarChart-Labs/chronicler.svg?branch=master)](https://travis-ci.org/StarChart-Labs/chronicler)

GitHub integration which validates whether release notes are updated when production code is changed

## Contributing

Information for how to contribute to Chronicler can be found in [the contribution guidelines](./CONTRIBUTING.md)

## Legal

Chronicler is distributed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0). The only requirement for use is inclusion of the following line within your NOTICES file:

```
StarChart-Labs Chronicler Web Application
Copyright 2017 StarChart Labs Authors.

This product includes software developed at
StarChart Labs (http://www.starchartlabs.org/).
```

The requirement for a copy of the license being included in distributions is fulfilled by a copy of the [LICENSE](./LICENSE) file being included in constructed JAR archives

## Reporting Vulnerabilities

If you discover a security vulnerability, contact the development team by e-mail at vulnerabilities@starchartlabs.org

## Use

To use Chronicler, [install](https://github.com/apps/chronicler-by-starchart-labs) it on one or more repositories. Pull requests made after installation will be analyzed for two conditions:

1. Did "production" files get modified?
2. Was a release notes file updated?

If "production" files were modified, and "release notes" were not, Chronicler will fail a status check on the pull request - otherwise, the pull request passes.

### Default Settings

Chronicler allows customization of what are considered production files and what is recognized as a release notes file via a YAML file in `/.starchart-labs/chronicler.yml`.

The default settings are:

- Everything in a `src` directory and NOT in a `test` directory is a production file
- Anything similar to `RELEASE_NOTES` or `CHANGE_LOG` is considered a release note file

The configuration for the default settings would look like this:

```
productionFiles:
   include:
      - '**/src/**'
   exclude:
      - '**/test/**'
releaseNoteFiles:
   include:
      - '**/CHANGE*LOG*'
      - '**/RELEASE*NOTES*'
```

Patterns use the Java PathMatcher [glob syntax](https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String)), which are similar to Ant directory patterns. All patterns are case-insensitive.
