package org.starchartlabs.chronicler.integration.github;

// TODO romeara doc, test
public final class Headers {

    public static final String WEBHOOK_EVENT_TYPE = "X-GitHub-Event";

    public static final String WEBHOOK_EVENT_ID = "X-GitHub-Delivery";

    public static final String WEBHOOK_SECURITY = "X-Hub-Signature";

    /**
     * Prevent instantiation of utility class
     */
    private Headers() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate instance of utility class '" + getClass().getName() + "'");
    }

}
