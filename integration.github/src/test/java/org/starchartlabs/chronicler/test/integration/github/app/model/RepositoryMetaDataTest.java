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
import org.starchartlabs.chronicler.integration.github.app.model.RepositoryMetaData;
import org.starchartlabs.chronicler.test.integration.github.ClasspathFileReader;
import org.starchartlabs.chronicler.test.integration.github.TestFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RepositoryMetaDataTest {

    private static final OwnerMetaData OWNER = new OwnerMetaData(100L, "login", "type", "url");

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullName() throws Exception {
        new RepositoryMetaData(1L, null, "fullName", false, "url", OWNER);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullFullName() throws Exception {
        new RepositoryMetaData(1L, "name", null, false, "url", OWNER);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullUrl() throws Exception {
        new RepositoryMetaData(1L, "name", "fullName", false, null, OWNER);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullOwner() throws Exception {
        new RepositoryMetaData(1L, "name", "fullName", false, "url", null);
    }

    @Test
    public void getTest() throws Exception {
        RepositoryMetaData result = new RepositoryMetaData(1L, "name", "fullName", false, "url", OWNER);

        Assert.assertEquals(result.getId(), 1L);
        Assert.assertEquals(result.getName(), "name");
        Assert.assertEquals(result.getFullName(), "fullName");
        Assert.assertEquals(result.isPrivateRepository(), false);
        Assert.assertEquals(result.getUrl(), "url");
        Assert.assertEquals(result.getOwner(), OWNER);
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        RepositoryMetaData result1 = new RepositoryMetaData(1L, "name", "fullName", false, "url", OWNER);
        RepositoryMetaData result2 = new RepositoryMetaData(1L, "name", "fullName", false, "url", OWNER);

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        RepositoryMetaData result = new RepositoryMetaData(1L, "name", "fullName", false, "url", OWNER);

        Assert.assertFalse(result.equals(null));
    }

    @Test
    public void equalsDifferentClass() throws Exception {
        RepositoryMetaData result = new RepositoryMetaData(1L, "name", "fullName", false, "url", OWNER);

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        RepositoryMetaData result = new RepositoryMetaData(1L, "name", "fullName", false, "url", OWNER);

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        RepositoryMetaData result1 = new RepositoryMetaData(1L, "name1", "fullName", false, "url", OWNER);
        RepositoryMetaData result2 = new RepositoryMetaData(1L, "name2", "fullName", false, "url", OWNER);

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        RepositoryMetaData result1 = new RepositoryMetaData(1L, "name", "fullName", false, "url", OWNER);
        RepositoryMetaData result2 = new RepositoryMetaData(1L, "name", "fullName", false, "url", OWNER);

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        RepositoryMetaData obj = new RepositoryMetaData(1L, "name", "fullName", false, "url", OWNER);

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("id=1"));
        Assert.assertTrue(result.contains("name=name"));
        Assert.assertTrue(result.contains("fullName=fullName"));
        Assert.assertTrue(result.contains("privateRepository=false"));
        Assert.assertTrue(result.contains("url=url"));
        Assert.assertTrue(result.contains("owner=" + OWNER.toString()));
    }

    @Test
    public void fromJson() throws Exception {
        String jsonString = ClasspathFileReader.readToString(TestFiles.REPOSITORY_META_DATA);

        RepositoryMetaData result = new ObjectMapper().readValue(jsonString, RepositoryMetaData.class);

        Assert.assertEquals(result.getId(), 27496774);
        Assert.assertEquals(result.getName(), "new-repository");
        Assert.assertEquals(result.getFullName(), "baxterandthehackers/new-repository");
        Assert.assertEquals(result.isPrivateRepository(), true);
        Assert.assertEquals(result.getUrl(), "https://api.github.com/repos/baxterandthehackers/new-repository");

        OwnerMetaData owner = result.getOwner();
        Assert.assertEquals(owner.getId(), 7649605L);
        Assert.assertEquals(owner.getLogin(), "baxterandthehackers");
        Assert.assertEquals(owner.getType(), "Organization");
        Assert.assertEquals(owner.getUrl(), "https://api.github.com/users/baxterandthehackers");
    }

}
