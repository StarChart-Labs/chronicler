/*
 * Copyright (c) Feb 13, 2019 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.calamari.core.paging;

import java.util.Collection;
import java.util.Iterator;

//TODO romeara alloy?
public interface PageProvider<T> extends Iterator<Collection<T>> {

    long estimateSize();

    PageProvider<T> trySplit();

}
