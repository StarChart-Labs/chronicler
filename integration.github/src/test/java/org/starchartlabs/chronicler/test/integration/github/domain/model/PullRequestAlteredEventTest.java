/*
 * Copyright 2017 StarChart Labs Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.starchartlabs.chronicler.test.integration.github.domain.model;

import org.starchartlabs.chronicler.integration.github.domain.model.PullRequestAlteredEvent;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PullRequestAlteredEventTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullUrl() throws Exception {
        new PullRequestAlteredEvent(1L, 2L, null, "statusesUrl");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullStatusesUrl() throws Exception {
        new PullRequestAlteredEvent(1L, 2L, "url", null);
    }

    @Test
    public void getTest() throws Exception {
        PullRequestAlteredEvent result = new PullRequestAlteredEvent(1L, 2L, "url", "statusesUrl");

        Assert.assertEquals(result.getId(), 1L);
        Assert.assertEquals(result.getNumber(), 2L);
        Assert.assertEquals(result.getUrl(), "url");
        Assert.assertEquals(result.getStatusesUrl(), "statusesUrl");
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        PullRequestAlteredEvent result1 = new PullRequestAlteredEvent(1L, 2L, "url", "statusesUrl");
        PullRequestAlteredEvent result2 = new PullRequestAlteredEvent(1L, 2L, "url", "statusesUrl");

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        PullRequestAlteredEvent result = new PullRequestAlteredEvent(1L, 2L, "url", "statusesUrl");

        Assert.assertFalse(result.equals(null));
    }

    @Test
    public void equalsDifferentClass() throws Exception {
        PullRequestAlteredEvent result = new PullRequestAlteredEvent(1L, 2L, "url", "statusesUrl");

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        PullRequestAlteredEvent result = new PullRequestAlteredEvent(1L, 2L, "url", "statusesUrl");

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        PullRequestAlteredEvent result1 = new PullRequestAlteredEvent(1L, 2L, "url1", "statusesUrl");
        PullRequestAlteredEvent result2 = new PullRequestAlteredEvent(1L, 2L, "url2", "statusesUrl");

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        PullRequestAlteredEvent result1 = new PullRequestAlteredEvent(1L, 2L, "url", "statusesUrl");
        PullRequestAlteredEvent result2 = new PullRequestAlteredEvent(1L, 2L, "url", "statusesUrl");

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        PullRequestAlteredEvent obj = new PullRequestAlteredEvent(1L, 2L, "url", "statusesUrl");

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("id=1"));
        Assert.assertTrue(result.contains("number=2"));
        Assert.assertTrue(result.contains("url=url"));
        Assert.assertTrue(result.contains("statusesUrl=statusesUrl"));
    }

}
