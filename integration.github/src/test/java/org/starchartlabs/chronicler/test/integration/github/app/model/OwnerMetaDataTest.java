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

import org.starchartlabs.chronicler.integration.github.app.model.OwnerMetaData;
import org.starchartlabs.chronicler.test.integration.github.ClasspathFileReader;
import org.starchartlabs.chronicler.test.integration.github.TestFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OwnerMetaDataTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullLogin() throws Exception {
        new OwnerMetaData(1L, null, "type", "url");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullType() throws Exception {
        new OwnerMetaData(1L, "login", null, "url");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullUrl() throws Exception {
        new OwnerMetaData(1L, "login", "type", null);
    }

    @Test
    public void getTest() throws Exception {
        OwnerMetaData result = new OwnerMetaData(1L, "login", "type", "url");

        Assert.assertEquals(result.getId(), 1L);
        Assert.assertEquals(result.getLogin(), "login");
        Assert.assertEquals(result.getType(), "type");
        Assert.assertEquals(result.getUrl(), "url");
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        OwnerMetaData result1 = new OwnerMetaData(1L, "login", "type", "url");
        OwnerMetaData result2 = new OwnerMetaData(1L, "login", "type", "url");

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        OwnerMetaData result = new OwnerMetaData(1L, "login", "type", "url");

        Assert.assertFalse(result.equals(null));
    }

    @Test
    public void equalsDifferentClass() throws Exception {
        OwnerMetaData result = new OwnerMetaData(1L, "login", "type", "url");

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        OwnerMetaData result = new OwnerMetaData(1L, "login", "type", "url");

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        OwnerMetaData result1 = new OwnerMetaData(1L, "login1", "type", "url");
        OwnerMetaData result2 = new OwnerMetaData(1L, "login2", "type", "url");

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        OwnerMetaData result1 = new OwnerMetaData(1L, "login", "type", "url");
        OwnerMetaData result2 = new OwnerMetaData(1L, "login", "type", "url");

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        OwnerMetaData obj = new OwnerMetaData(1L, "login", "type", "url");

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("id=1"));
        Assert.assertTrue(result.contains("login=login"));
        Assert.assertTrue(result.contains("type=type"));
        Assert.assertTrue(result.contains("url=url"));
    }

    @Test
    public void fromJson() throws Exception {
        String jsonString = ClasspathFileReader.readToString(TestFiles.OWNER_META_DATA);

        OwnerMetaData result = new ObjectMapper().readValue(jsonString, OwnerMetaData.class);

        Assert.assertEquals(result.getId(), 1L);
        Assert.assertEquals(result.getLogin(), "octocat");
        Assert.assertEquals(result.getType(), "User");
        Assert.assertEquals(result.getUrl(), "https://api.github.com/users/octocat");
    }

}
