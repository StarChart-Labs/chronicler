/*
 * Copyright (c) Sep 26, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.calamari.core.paging;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starchartlabs.alloy.core.MoreObjects;

/**
 * Handles parsing of GitHub paging headers into distinct links for traversal of paged data
 *
 * <p>
 * GitHub does not provide all headers on all returns. The first page only contains the "next" and "last" links. The
 * last page only contains the "first" and "prev" links. All other pages contain all four headers. If there is only one
 * page of data, none of the links are returned
 *
 * @author romeara
 * @since 0.1.0
 */
public class PagingLinks {

    // Pattern which matches link header format returned by GitHub and allows extraction of the URL and rel key
    // Example value: <https://api.github.com/user/repos?page=1&per_page=100>; rel="first"
    // Example extraction: https://api.github.com/user/repos?page=1&per_page=100, first
    private static final Pattern LINK_PATTERN = Pattern
            .compile("\\A.*<([A-Za-z0-9\\-._~:/?#\\[\\]@!$&'()*+,;=%]*)>; rel=\"([A-Za-z0-9]*)\"");

    private static final String FIRST_PAGE_REL = "first";

    private static final String PREVIOUS_PAGE_REL = "prev";

    private static final String NEXT_PAGE_REL = "next";

    private static final String LAST_PAGE_REL = "last";

    /** Logger reference to output information to the application log files */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Optional<String> firstPageUrl;

    private final Optional<String> previousPageUrl;

    private final Optional<String> nextPageUrl;

    private final Optional<String> lastPageUrl;

    /**
     * Parses one or more paging links from GitHub response headers
     *
     * @param links
     *            One or more "Link" header values. Supports separated headers and headers contains a CSV of multiple
     *            link entries
     * @since 0.1.0
     */
    public PagingLinks(Collection<String> links) {
        Objects.requireNonNull(links);

        logger.debug("Link Headers: {}", links);

        String firstUrl = null;
        String prevUrl = null;
        String nextUrl = null;
        String lastUrl = null;

        Collection<String> allLinks = links.stream()
                .flatMap(s -> Arrays.asList(s.split(",")).stream())
                .map(String::trim)
                .collect(Collectors.toSet());

        for (String link : allLinks) {
            Matcher matcher = LINK_PATTERN.matcher(link);

            if (matcher.matches()) {
                String href = matcher.group(1);
                String rel = matcher.group(2);

                if (Objects.equals(rel, FIRST_PAGE_REL)) {
                    firstUrl = href;
                } else if (Objects.equals(rel, PREVIOUS_PAGE_REL)) {
                    prevUrl = href;
                } else if (Objects.equals(rel, NEXT_PAGE_REL)) {
                    nextUrl = href;
                } else if (Objects.equals(rel, LAST_PAGE_REL)) {
                    lastUrl = href;
                }
            }
        }

        firstPageUrl = Optional.ofNullable(firstUrl);
        previousPageUrl = Optional.ofNullable(prevUrl);
        nextPageUrl = Optional.ofNullable(nextUrl);
        lastPageUrl = Optional.ofNullable(lastUrl);
    }

    /**
     * @return URL to the first set of data in a paged sequence. Only present if the response providing the links was
     *         not the first page
     * @since 0.1.0
     */
    public Optional<String> getFirstPageUrl() {
        return firstPageUrl;
    }

    /**
     * @return URL to the previous set of data in a paged sequence. Only present if the response providing the links was
     *         not the first page
     * @since 0.1.0
     */
    public Optional<String> getPreviousPageUrl() {
        return previousPageUrl;
    }

    /**
     * @return URL to the next set of data in a paged sequence. Only present if the response providing the links was not
     *         the last page
     * @since 0.1.0
     */
    public Optional<String> getNextPageUrl() {
        return nextPageUrl;
    }

    /**
     * @return URL to the last set of data in a paged sequence. Only present if the response providing the links was not
     *         the last page
     * @since 0.1.0
     */
    public Optional<String> getLastPageUrl() {
        return lastPageUrl;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstPageUrl(),
                getPreviousPageUrl(),
                getNextPageUrl(),
                getLastPageUrl());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof PagingLinks) {
            PagingLinks compare = (PagingLinks) obj;

            result = Objects.equals(compare.getFirstPageUrl(), getFirstPageUrl())
                    && Objects.equals(compare.getPreviousPageUrl(), getPreviousPageUrl())
                    && Objects.equals(compare.getNextPageUrl(), getNextPageUrl())
                    && Objects.equals(compare.getLastPageUrl(), getLastPageUrl());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("firstPageUrl", getFirstPageUrl())
                .add("previousPageUrl", getPreviousPageUrl())
                .add("nextPageUrl", getNextPageUrl())
                .add("lastPageUrl", getLastPageUrl())
                .toString();
    }

}
