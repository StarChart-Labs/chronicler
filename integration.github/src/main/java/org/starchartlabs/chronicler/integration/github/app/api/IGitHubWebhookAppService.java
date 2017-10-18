/*
 * Copyright (c) Oct 17, 2017 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.integration.github.app.api;

//TODO romeara doc
public interface IGitHubWebhookAppService {

    boolean acceptPayload(String securityKey, String eventType, String payload);

}
