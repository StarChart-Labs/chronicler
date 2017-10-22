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
package org.starchartlabs.chronicler.test.integration.github.app.model;

import org.starchartlabs.chronicler.integration.github.app.model.PullRequestEvent;
import org.starchartlabs.chronicler.integration.github.app.model.PullRequestMetaData;
import org.starchartlabs.chronicler.test.integration.github.ClasspathFileReader;
import org.starchartlabs.chronicler.test.integration.github.TestFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PullRequestEventTest {

    private static final PullRequestMetaData PULL_REQUEST_META_DATA = new PullRequestMetaData(1L, 2L, "url",
            "statusesUrl");

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullAction() throws Exception {
        new PullRequestEvent(null, PULL_REQUEST_META_DATA);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullPullRequest() throws Exception {
        new PullRequestEvent("action", null);
    }

    @Test
    public void getTest() throws Exception {
        PullRequestEvent result = new PullRequestEvent("action", PULL_REQUEST_META_DATA);

        Assert.assertEquals(result.getAction(), "action");
        Assert.assertEquals(result.getPullRequest(), PULL_REQUEST_META_DATA);
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        PullRequestEvent result1 = new PullRequestEvent("action", PULL_REQUEST_META_DATA);
        PullRequestEvent result2 = new PullRequestEvent("action", PULL_REQUEST_META_DATA);

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        PullRequestEvent result = new PullRequestEvent("action", PULL_REQUEST_META_DATA);

        Assert.assertFalse(result.equals(null));
    }

    @Test
    public void equalsDifferentClass() throws Exception {
        PullRequestEvent result = new PullRequestEvent("action", PULL_REQUEST_META_DATA);

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        PullRequestEvent result = new PullRequestEvent("action", PULL_REQUEST_META_DATA);

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        PullRequestEvent result1 = new PullRequestEvent("action1", PULL_REQUEST_META_DATA);
        PullRequestEvent result2 = new PullRequestEvent("action2", PULL_REQUEST_META_DATA);

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        PullRequestEvent result1 = new PullRequestEvent("action", PULL_REQUEST_META_DATA);
        PullRequestEvent result2 = new PullRequestEvent("action", PULL_REQUEST_META_DATA);

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        PullRequestEvent obj = new PullRequestEvent("action", PULL_REQUEST_META_DATA);

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("action=action"));
        Assert.assertTrue(result.contains("pullRequest=" + PULL_REQUEST_META_DATA.toString()));
    }

    @Test
    public void fromJson() throws Exception {
        String jsonString = ClasspathFileReader.readToString(TestFiles.PULL_REQUEST_EVENT);

        PullRequestEvent result = new ObjectMapper().readValue(jsonString, PullRequestEvent.class);

        Assert.assertEquals(result.getAction(), "opened");
        Assert.assertEquals(result.getPullRequest().getId(), 34778301L);
        Assert.assertEquals(result.getPullRequest().getNumber(), 1L);
        Assert.assertEquals(result.getPullRequest().getUrl(),
                "https://api.github.com/repos/baxterthehacker/public-repo/pulls/1");
        Assert.assertEquals(result.getPullRequest().getStatusesUrl(),
                "https://api.github.com/repos/baxterthehacker/public-repo/statuses/0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c");
    }

}
