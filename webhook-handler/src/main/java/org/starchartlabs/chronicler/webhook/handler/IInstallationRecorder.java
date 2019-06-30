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

//TODO romeara doc
public interface IInstallationRecorder {

    void installedOnAll(String login);

    void partialInstallation(String login, int repositoryCount);

    void partialUninstallation(String login, int repositoryCount);

    void uninstallation(String login);

}
