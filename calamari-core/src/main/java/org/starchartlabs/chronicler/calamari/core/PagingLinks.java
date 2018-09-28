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
package org.starchartlabs.chronicler.calamari.core;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starchartlabs.alloy.core.MoreObjects;

import okhttp3.HttpUrl;

//TODO romeara
public class PagingLinks {

    private static final Pattern LINK_PATTERN = Pattern
            .compile("\\A.*<([A-Za-z0-9\\-._~:/?#\\[\\]@!$&'()*+,;=%]*)>; rel=\"([A-Za-z0-9]*)\"");

    private static final String NEXT_PAGE_REL = "next";

    private static final String LAST_PAGE_REL = "last";

    /** Logger reference to output information to the application log files */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Optional<String> nextPageUrl;

    private final Optional<String> lastPageUrl;

    public PagingLinks(Collection<String> links) {
        Objects.requireNonNull(links);
        logger.debug("GitHub Paging Headers: {}", links);

        String nextUrl = null;
        String lastUrl = null;

        for (String link : links) {
            Matcher matcher = LINK_PATTERN.matcher(link);

            if (matcher.matches()) {
                String href = matcher.group(1);
                String rel = matcher.group(2);

                if (Objects.equals(rel, NEXT_PAGE_REL)) {
                    nextUrl = href;
                } else if (Objects.equals(rel, LAST_PAGE_REL)) {
                    lastUrl = href;
                }
            }
        }

        // If the next page IS the last page, the link headers do not duplicate the URL
        nextUrl = Optional.ofNullable(nextUrl).orElse(lastUrl);

        nextPageUrl = Optional.ofNullable(nextUrl);
        lastPageUrl = Optional.ofNullable(lastUrl);
    }

    public Optional<String> getNextPageUrl() {
        return nextPageUrl;
    }

    public Optional<String> getLastPageUrl() {
        return lastPageUrl;
    }

    public boolean isEmpty() {
        return !getNextPageUrl().isPresent()
                && !getLastPageUrl().isPresent();
    }

    public boolean isLastPage(@Nullable String url) {
        Optional<HttpUrl> lastPage = getLastPageUrl()
                .map(HttpUrl::get);
        Optional<HttpUrl> targetPage = Optional.ofNullable(url)
                .map(HttpUrl::get);

        String lastPageNumber = lastPage
                .map(u -> u.queryParameter("page"))
                .orElse(null);

        String lastPagePerPage = lastPage
                .map(u -> u.queryParameter("per_page"))
                .orElse(null);

        String targetPageNumber = targetPage
                .map(u -> u.queryParameter("page"))
                .orElse(null);

        String targetPagePerPage = targetPage
                .map(u -> u.queryParameter("per_page"))
                .orElse(null);

        // We only check for page number null, not per page, as null per page is equivalent (default) and will result in
        // the same number of pages in both cases
        return Objects.equals(lastPageNumber, targetPageNumber)
                && Objects.equals(lastPagePerPage, targetPagePerPage)
                && lastPageNumber != null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNextPageUrl(),
                getLastPageUrl());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof PagingLinks) {
            PagingLinks compare = (PagingLinks) obj;

            result = Objects.equals(compare.getNextPageUrl(), getNextPageUrl())
                    && Objects.equals(compare.getLastPageUrl(), getLastPageUrl());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("nextPageUrl", getNextPageUrl())
                .add("lastPageUrl", getLastPageUrl())
                .toString();
    }

}
