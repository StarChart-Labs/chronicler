# Credential Lookup Keys

Chronicler uses the [lockdown](https://github.com/StarChart-Labs/lockdown) library to encrypt credential information needed by the application. These credentials are identified by lookup keys:

- GITHUB-WEBHOOK
 - Key storing webhook secret information used to verify that payloads originated from the GitHub application