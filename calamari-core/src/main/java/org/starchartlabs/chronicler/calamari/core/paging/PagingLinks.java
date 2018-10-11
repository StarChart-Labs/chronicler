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
//1 of 34: no first, no prev, next, last
//2 of 34: first, prev, next, last
//32 of 34: first, prev, next, last
//33 of 34: first, prev, next, last
//34 of 34: first, prev, no next, no last
public class PagingLinks {

    private static final Pattern LINK_PATTERN = Pattern
            .compile("\\A.*<([A-Za-z0-9\\-._~:/?#\\[\\]@!$&'()*+,;=%]*)>; rel=\"([A-Za-z0-9]*)\"");

    private static final String FIRST_PAGE_REL = "first";

    private static final String PREVIOUS_PAGE_REL = "prev";

    private static final String NEXT_PAGE_REL = "next";

    private static final String LAST_PAGE_REL = "last";

    /** Logger reference to output information to the application log files */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String url;

    private final Optional<String> firstPageUrl;

    private final Optional<String> previousPageUrl;

    private final Optional<String> nextPageUrl;

    private final Optional<String> lastPageUrl;

    // TODO romeara - maybe pass in url with the links, and filter the next URL at creation here? so can just have the
    // "next" url field, and that's it?
    public PagingLinks(String url, Collection<String> links) {
        this.url = Objects.requireNonNull(url);

        Objects.requireNonNull(links);
        logger.debug("GitHub Paging Headers: {}", links);

        String firstUrl = null;
        String prevUrl = null;
        String nextUrl = null;
        String lastUrl = null;

        for (String link : links) {
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

        // If the next page IS the last page, the link headers do not always duplicate the URL
        prevUrl = Optional.ofNullable(prevUrl).orElse(firstUrl);
        nextUrl = Optional.ofNullable(nextUrl).orElse(lastUrl);

        firstPageUrl = Optional.ofNullable(firstUrl);
        previousPageUrl = Optional.ofNullable(prevUrl)
                .filter(prev -> !isSamePage(url, getFirstPageUrl().orElse(null)));
        nextPageUrl = Optional.ofNullable(nextUrl)
                .filter(next -> !isSamePage(url, getLastPageUrl().orElse(null)));
        lastPageUrl = Optional.ofNullable(lastUrl);
    }

    public String getUrl() {
        return url;
    }

    public Optional<String> getFirstPageUrl() {
        return firstPageUrl;
    }

    public Optional<String> getPreviousPageUrl() {
        return previousPageUrl;
    }

    public Optional<String> getNextPageUrl() {
        return nextPageUrl;
    }

    public Optional<String> getLastPageUrl() {
        return lastPageUrl;
    }

    private boolean isSamePage(@Nullable String url1, @Nullable String url2) {
        Integer page1Number = Optional.ofNullable(url1)
                .flatMap(PagingLinks::getPageNumber)
                .orElse(null);
        Integer page2Number = Optional.ofNullable(url2)
                .flatMap(PagingLinks::getPageNumber)
                .orElse(null);

        Integer page1PerPage = Optional.ofNullable(url1)
                .flatMap(PagingLinks::getPerPage)
                .orElse(null);
        Integer page2PerPage = Optional.ofNullable(url2)
                .flatMap(PagingLinks::getPerPage)
                .orElse(null);

        // Nulls indicate defaults, which are consistent
        return Objects.equals(page1Number, page2Number)
                && Objects.equals(page1PerPage, page2PerPage);
    }

    public static Optional<Integer> getPageNumber(String url) {
        Objects.requireNonNull(url);

        return Optional.of(url)
                .map(HttpUrl::get)
                .map(u -> u.queryParameter("page"))
                .map(Integer::valueOf);
    }

    public static Optional<Integer> getPerPage(String url) {
        Objects.requireNonNull(url);

        return Optional.of(url)
                .map(HttpUrl::get)
                .map(u -> u.queryParameter("per_page"))
                .map(Integer::valueOf);
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
