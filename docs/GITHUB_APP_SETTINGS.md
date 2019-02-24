# GitHub App Settings

Chronicler is implemented as a (GitHub App)[https://developer.github.com/apps/] with the following settings:

## Permissions

- Repository metadata: 		`read`
- Commit statues:			`read` & `write`
- Pull requests:  			`read` & `write`
- Single file:				'read'
 - Path: `/.starchart-labs/chronicler.yml`

All other permissions are set to `no access`

## Event Subscriptions

- Repository
- Pull request