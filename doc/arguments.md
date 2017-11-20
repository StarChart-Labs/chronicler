# Arguments

Chronicler has several custom arguments for it's execution, in addition to standard Spring Boot arguments. These can be provided via standard Spring Boot patterns (command line, properties files, etc)

## Required

- org.starchartlabs.chronicler.credentials
 - The location of the encrypted credentials file for the application. Must have keys defined in [credential-lookup-keys](./credential-lookup-keys.md)
 - Default: None
- org.starchartlabs.chronicler.credentials.key
 - The location of the private RSA key to use when decrypting credentials stored in the file specified in `org.starchartlabs.chronicler.credentials`
 - Default: None