/*
 * Copyright (c) Jun 30, 2019 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.webhook.handler;

/**
 * Represents a handler which responds to changes in installation state for a GitHub App
 *
 * @author romeara
 * @since 0.5.0
 */
public interface IInstallationRecorder {

    /**
     * Records a net-new installation of the App
     *
     * @param login
     *            The name of the user or organization installing the App
     * @since 0.5.0
     */
    void installedOnAll(String login);

    /**
     * Records addition of repositories to an installation of the App
     *
     * @param login
     *            The name of the user or organization installing the App on select repositories
     * @param repositoryCount
     *            The number of repositories added
     * @since 0.5.0
     */
    void partialInstallation(String login, int repositoryCount);

    /**
     * Records removal of repositories from an installation of the App
     *
     * @param login
     *            The name of the user or organization un-installing the App for select repositories
     * @param repositoryCount
     *            The number of repositories removed
     * @since 0.5.0
     */
    void partialUninstallation(String login, int repositoryCount);

    /**
     * Records a full un-installation of a GitHub App
     *
     * @param login
     *            The name of the user or organization un-installing the App
     * @since 0.5.0
     */
    void uninstallation(String login);

}
