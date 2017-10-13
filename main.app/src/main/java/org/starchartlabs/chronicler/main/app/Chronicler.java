/*
 * Copyright (c) May 3, 2017 Corona IDE.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * "romeara" - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.main.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

// TODO romeara doc
@SpringBootApplication
@Import({ MainAppServerConfiguration.class })
public class Chronicler {

    public static void main(String[] args) {
        SpringApplication.run(Chronicler.class, args);
    }

}
