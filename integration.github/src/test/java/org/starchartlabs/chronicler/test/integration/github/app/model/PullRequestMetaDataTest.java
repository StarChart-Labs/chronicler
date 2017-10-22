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

import org.starchartlabs.chronicler.integration.github.app.model.PullRequestMetaData;
import org.starchartlabs.chronicler.test.integration.github.ClasspathFileReader;
import org.starchartlabs.chronicler.test.integration.github.TestFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PullRequestMetaDataTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullUrl() throws Exception {
        new PullRequestMetaData(1L, 2L, null, "statusesUrl");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullStatusesUrl() throws Exception {
        new PullRequestMetaData(1L, 2L, "url", null);
    }

    @Test
    public void getTest() throws Exception {
        PullRequestMetaData result = new PullRequestMetaData(1L, 2L, "url", "statusesUrl");

        Assert.assertEquals(result.getId(), 1L);
        Assert.assertEquals(result.getNumber(), 2L);
        Assert.assertEquals(result.getUrl(), "url");
        Assert.assertEquals(result.getStatusesUrl(), "statusesUrl");
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        PullRequestMetaData result1 = new PullRequestMetaData(1L, 2L, "url", "statusesUrl");
        PullRequestMetaData result2 = new PullRequestMetaData(1L, 2L, "url", "statusesUrl");

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        PullRequestMetaData result = new PullRequestMetaData(1L, 2L, "url", "statusesUrl");

        Assert.assertFalse(result.equals(null));
    }

    @Test
    public void equalsDifferentClass() throws Exception {
        PullRequestMetaData result = new PullRequestMetaData(1L, 2L, "url", "statusesUrl");

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        PullRequestMetaData result = new PullRequestMetaData(1L, 2L, "url", "statusesUrl");

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        PullRequestMetaData result1 = new PullRequestMetaData(1L, 2L, "url1", "statusesUrl");
        PullRequestMetaData result2 = new PullRequestMetaData(1L, 2L, "url2", "statusesUrl");

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        PullRequestMetaData result1 = new PullRequestMetaData(1L, 2L, "url", "statusesUrl");
        PullRequestMetaData result2 = new PullRequestMetaData(1L, 2L, "url", "statusesUrl");

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        PullRequestMetaData obj = new PullRequestMetaData(1L, 2L, "url", "statusesUrl");

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("id=1"));
        Assert.assertTrue(result.contains("number=2"));
        Assert.assertTrue(result.contains("url=url"));
        Assert.assertTrue(result.contains("statusesUrl=statusesUrl"));
    }

    @Test
    public void fromJson() throws Exception {
        String jsonString = ClasspathFileReader.readToString(TestFiles.PULL_REQUEST_META_DATA);

        PullRequestMetaData result = new ObjectMapper().readValue(jsonString, PullRequestMetaData.class);

        Assert.assertEquals(result.getId(), 34778301L);
        Assert.assertEquals(result.getNumber(), 1L);
        Assert.assertEquals(result.getUrl(), "https://api.github.com/repos/baxterthehacker/public-repo/pulls/1");
        Assert.assertEquals(result.getStatusesUrl(),
                "https://api.github.com/repos/baxterthehacker/public-repo/statuses/0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c");
    }
}
