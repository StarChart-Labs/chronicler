# Contributing

Chronicler is currently in pre-alpha development - basic functionality is still being implemented.

## Basic Requirements

Pull requests should include:

- Minimum side-effect changes
- Documented methods, classes, etc
- Tests (where possible)
- Changelog entries

## Libraries Used

Chronicler is currently written against Java 1.8. Testing use the TestNG framework, and the mock testing library of choice is mockito

## Development Environment Setup

It is recommended to create an isolated Eclipse workspace for Chronicler. You should also import the standard formatting and save settings from eclipseConfiguration:

- `Java > CodeStyle > Cleanup` is imported from cleanup.xml
- `Java > CodeStyle > Formatter` is imported from codeformatter.xml
- `Java > CodeStyle > Code Templates` is imported from codetemplates.xml

It is also recommended to turn on "save actions", under the Java Editor settings, to format saved lines and perform other cleanup operations

Once settings are configured, use the Eclipse BuildShip plug-in to import all projects from the root of the repository
