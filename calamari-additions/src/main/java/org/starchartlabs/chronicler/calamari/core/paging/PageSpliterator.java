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

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO romeara alloy?
public class PageSpliterator<T> implements Spliterator<T> {

    /** Logger reference to output information to the application log files */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final PageProvider<T> pageProvider;

    private LinkedList<T> elements;

    public PageSpliterator(PageProvider<T> pageProvider) {
        this.pageProvider = Objects.requireNonNull(pageProvider);
        elements = new LinkedList<>();
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        // Populate next set of elements, if possible
        if (elements.isEmpty() && pageProvider.hasNext()) {
            elements.addAll(pageProvider.next());

            // TODO romeara reduce logging level after verification
            logger.info("Read {} paged elements", elements.size());
        }

        T element = elements.poll();

        if (element != null) {
            action.accept(element);
        }

        return (element != null);
    }

    @Override
    public Spliterator<T> trySplit() {
        return Optional.ofNullable(pageProvider.trySplit())
                .map(a -> new PageSpliterator<T>(a))
                .orElse(null);
    }

    @Override
    public long estimateSize() {
        return pageProvider.estimateSize();
    }

    @Override
    public int characteristics() {
        return Spliterator.IMMUTABLE | Spliterator.ORDERED;
    }

}
