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

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO romeara alloy?
public class ShortCircuitSpliterator<T, A> implements Spliterator<T> {

    private static final int SUPPORTED_CHARACTERISTICS = Spliterator.DISTINCT | Spliterator.IMMUTABLE
            | Spliterator.NONNULL | Spliterator.ORDERED | Spliterator.SORTED;

    /** Logger reference to output information to the application log files */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Spliterator<T> spliterator;

    private final Predicate<A> completedPredicate;

    private final AccumulatingConsumer<T, A> consumer;

    public ShortCircuitSpliterator(Spliterator<T> spliterator, BiFunction<Optional<A>, ? super T, A> accumulator,
            Predicate<A> completedPredicate) {
        this.spliterator = Objects.requireNonNull(spliterator);
        this.completedPredicate = Objects.requireNonNull(completedPredicate);
        this.consumer = new AccumulatingConsumer<>(accumulator);
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        boolean complete = consumer.getAccumulated()
                .map(completedPredicate::test)
                .orElse(false);

        if (complete) {
            // TODO romeara reduce logging level once verified
            logger.info("Completion condition met - short circuiting spliterator advancement");
        } else {
            complete = !spliterator.tryAdvance(input -> consumer.accept(action, input));

            if (complete) {
                // TODO romeara reduce logging level once verified
                logger.info("Spliterator terminated normally, short circuiting condition never met");
            }
        }

        return !complete;
    }

    @Override
    public Spliterator<T> trySplit() {
        // TODO romeara Not sure there is a good way to safely split this for parallel operation - for now, stick to the
        // safer option
        return null;
    }

    @Override
    public long estimateSize() {
        // There is no way to know when the condition will be met, so present the worst-case scenario (of "never short
        // circuited") according to the underlying spliterator
        return spliterator.estimateSize();
    }

    @Override
    public int characteristics() {
        // Certain characteristic behaviors do not persist once short-circuiting is applied
        // For example, SIZED can no longer be guaranteed, as the traversal may complete before iterating over all
        // underlying values
        return SUPPORTED_CHARACTERISTICS & spliterator.characteristics();
    }

    // Expose information available in the supporting spliterator, allowing the "SORTED" characteristic to be supported
    // if provided by the supporting spliterator
    @Override
    public Comparator<? super T> getComparator() {
        return spliterator.getComparator();
    }

    private static final class AccumulatingConsumer<T, A> {

        private final BiFunction<Optional<A>, ? super T, A> accumulator;

        private Optional<A> accumulated;

        public AccumulatingConsumer(BiFunction<Optional<A>, ? super T, A> accumulator) {
            this.accumulator = Objects.requireNonNull(accumulator);
            this.accumulated = Optional.empty();
        }

        public void accept(Consumer<? super T> action, T input) {
            action.accept(input);
            accumulated = Optional.of(accumulator.apply(accumulated, input));
        }

        public Optional<A> getAccumulated() {
            return accumulated;
        }

    }

}
