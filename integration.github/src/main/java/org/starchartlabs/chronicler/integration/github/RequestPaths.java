package org.starchartlabs.chronicler.integration.github;

// TODO romeara doc, test
public final class RequestPaths {

    public static final String WEBHOOK = "/webhook";

    /**
     * Prevent instantiation of utility class
     */
    private RequestPaths() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate instance of utility class '" + getClass().getName() + "'");
    }

}
