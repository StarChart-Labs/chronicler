/*
 * Copyright (c) Oct 11, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.test.calamari.core.paging;

import java.util.Arrays;

import org.starchartlabs.chronicler.calamari.core.paging.PagingLinks;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PagingLinksTest {

    private static final String FIRST_PAGE_LINK = "https://api.github.com/user/repos?page=1&per_page=100";

    private static final String PREV_PAGE_LINK = "https://api.github.com/user/repos?page=2&per_page=100";

    private static final String NEXT_PAGE_LINK = "https://api.github.com/user/repos?page=4&per_page=100";

    private static final String LAST_PAGE_LINK = "https://api.github.com/user/repos?page=50&per_page=100";

    private static final String FIRST_PAGE_HEADER = getLinkHeader(FIRST_PAGE_LINK, "first");

    private static final String PREV_PAGE_HEADER = getLinkHeader(PREV_PAGE_LINK, "prev");

    private static final String NEXT_PAGE_HEADER = getLinkHeader(NEXT_PAGE_LINK, "next");

    private static final String LAST_PAGE_HEADER = getLinkHeader(LAST_PAGE_LINK, "last");

    private static final String MULTIPLE_HEADERS = NEXT_PAGE_HEADER + ", " + LAST_PAGE_HEADER;

    private static final String ALL_HEADERS = FIRST_PAGE_HEADER + ", " + PREV_PAGE_HEADER + ", " + NEXT_PAGE_HEADER
            + ", " + LAST_PAGE_HEADER;

    @Test
    public void firstPageOnly() throws Exception {
        PagingLinks result = new PagingLinks(Arrays.asList(FIRST_PAGE_HEADER));

        Assert.assertEquals(result.getFirstPageUrl().get(), FIRST_PAGE_LINK);
        Assert.assertFalse(result.getPreviousPageUrl().isPresent());
        Assert.assertFalse(result.getLastPageUrl().isPresent());
        Assert.assertFalse(result.getNextPageUrl().isPresent());
    }

    @Test
    public void prevPageOnly() throws Exception {
        PagingLinks result = new PagingLinks(Arrays.asList(PREV_PAGE_HEADER));

        Assert.assertFalse(result.getFirstPageUrl().isPresent());
        Assert.assertEquals(result.getPreviousPageUrl().get(), PREV_PAGE_LINK);
        Assert.assertFalse(result.getLastPageUrl().isPresent());
        Assert.assertFalse(result.getNextPageUrl().isPresent());
    }

    @Test
    public void nextPageOnly() throws Exception {
        PagingLinks result = new PagingLinks(Arrays.asList(NEXT_PAGE_HEADER));

        Assert.assertFalse(result.getFirstPageUrl().isPresent());
        Assert.assertFalse(result.getPreviousPageUrl().isPresent());
        Assert.assertFalse(result.getLastPageUrl().isPresent());
        Assert.assertEquals(result.getNextPageUrl().get(), NEXT_PAGE_LINK);
    }

    @Test
    public void lastPageOnly() throws Exception {
        PagingLinks result = new PagingLinks(Arrays.asList(LAST_PAGE_HEADER));

        Assert.assertFalse(result.getFirstPageUrl().isPresent());
        Assert.assertFalse(result.getPreviousPageUrl().isPresent());
        Assert.assertFalse(result.getNextPageUrl().isPresent());
        Assert.assertEquals(result.getLastPageUrl().get(), LAST_PAGE_LINK);
    }

    @Test
    public void validLinkHeadersSingleEntry() throws Exception {
        PagingLinks result = new PagingLinks(Arrays.asList(MULTIPLE_HEADERS));

        Assert.assertFalse(result.getFirstPageUrl().isPresent());
        Assert.assertFalse(result.getPreviousPageUrl().isPresent());
        Assert.assertEquals(result.getNextPageUrl().get(), NEXT_PAGE_LINK);
        Assert.assertEquals(result.getLastPageUrl().get(), LAST_PAGE_LINK);
    }

    @Test
    public void validLinkHeadersMultiEntry() throws Exception {
        PagingLinks result = new PagingLinks(Arrays.asList(NEXT_PAGE_HEADER, LAST_PAGE_HEADER));

        Assert.assertFalse(result.getFirstPageUrl().isPresent());
        Assert.assertFalse(result.getPreviousPageUrl().isPresent());
        Assert.assertEquals(result.getNextPageUrl().get(), NEXT_PAGE_LINK);
        Assert.assertEquals(result.getLastPageUrl().get(), LAST_PAGE_LINK);
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        PagingLinks result1 = new PagingLinks(Arrays.asList(ALL_HEADERS));
        PagingLinks result2 = new PagingLinks(Arrays.asList(ALL_HEADERS));

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        PagingLinks result = new PagingLinks(Arrays.asList(ALL_HEADERS));

        Assert.assertFalse(result.equals(null));
    }

    @Test
    public void equalsDifferentClass() throws Exception {
        PagingLinks result = new PagingLinks(Arrays.asList(ALL_HEADERS));

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        PagingLinks result = new PagingLinks(Arrays.asList(ALL_HEADERS));

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        PagingLinks result1 = new PagingLinks(Arrays.asList(ALL_HEADERS));
        PagingLinks result2 = new PagingLinks(Arrays.asList(MULTIPLE_HEADERS));

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        PagingLinks result1 = new PagingLinks(Arrays.asList(ALL_HEADERS));
        PagingLinks result2 = new PagingLinks(Arrays.asList(ALL_HEADERS));

        Assert.assertTrue(result1.equals(result2));
    }

    private static String getLinkHeader(String link, String rel) {
        return "<" + link + ">; rel=\"" + rel + "\"";
    }

}
