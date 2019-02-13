# Code Release Process

* Run a full build via `./gradlew clean build`
* Update the version number to remove the "-SNAPSHOT" designation. All version numbers should be a fully-qualified semantic version of form `<major>.<minor>.<micro>`
* Change the header "Unreleased" in CHANGE_LOG.md to the target release number, and create a new "Unreleased" header above it
* Commit the version number and CHANGE_LOG updates
* Tag the git repository with the fully-qualified semantic version number
* Change version number to `<released version> + 1 micro` + `-SNAPSHOT`
* Commit to git
* Push changes and tag to GitHub
* Create a release on GitHub including all binary and source jars

# Deployment

Deployment of Chronicler is done in the context of a "stage". A "stage" is something like "dev", "test", or "production" - a distinct environment where a full copy of the application will be independently deployed and run.

# One Time Setup

To deploy Chronicler for a given stage, the following manual setup is required once:

 - Create a GitHub App configuration for the stage
 	- Using the description in `github-app.yml`, setup a GitHub app, replacing the title and URLs to match the stage being setup
 - Configure a Webhook Secret and download the secret key
 	- These will be added to AWS in the next step, retain them until that time
 - On [AWS System Manager](https://console.aws.amazon.com/systems-manager), login with an admin StarChart-Labs account and create the following values in Parameter Store:
 	- /${stage}/chronicler/github/app/id
 		- The GitHub App ID (available in the General section of the App's information, in "About")
 	- /${stage}/chronicler/github/app/key
 		- The contents of the secret key downloaded from the App, WITHOUT the leading BEGIN/END key headers and footers (Parameter store doesn't seem to deal with newlines, this and code within the App that re-adds them is a workaround)
 	- /${stage}/chronicler/github/webhook-secret
 		- The webhook secret configured on the GitHub App
 - At this point, local copies of the secret and key may be discarded - they can be regenerated and configured later if necessary
 - The last one-time step will be to go to [AWS API Gateway Custom Domain Names]
   - Create the appropriate domain name
   - Find the "Target Domain Name" entry, and add a DNS record on Google Domains for the target URL
 	
# Serverless Deployment

Assuming the serverless command line tool has been installed, and setup for authentication as per the Serverless instructions, updated code can be deployed by:

 - Running `./gradlew clean build` to rebuild deployable ZIP archives
 - Running `serverless deploy` to deploy the new set of code
