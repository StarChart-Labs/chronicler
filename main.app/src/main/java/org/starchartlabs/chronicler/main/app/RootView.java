package org.starchartlabs.chronicler.main.app;

import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

// TODO romeara doc, test, add meta-data view
public class RootView {

    @JsonProperty(value = "installationUrl", required = true)
    private final String installationUrl;

    public RootView(String installationUrl) {
        this.installationUrl = Objects.requireNonNull(installationUrl);
    }

    public String getInstallationUrl() {
        return installationUrl;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInstallationUrl());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof RootView) {
            RootView compare = (RootView) obj;

            result = Objects.equals(compare.getInstallationUrl(), getInstallationUrl());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("installationUrl", getInstallationUrl())
                .toString();
    }
}
