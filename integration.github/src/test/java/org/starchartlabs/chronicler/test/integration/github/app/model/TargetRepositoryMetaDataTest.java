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

import org.starchartlabs.chronicler.integration.github.app.model.TargetRepositoryMetaData;
import org.starchartlabs.chronicler.test.integration.github.ClasspathFileReader;
import org.starchartlabs.chronicler.test.integration.github.TestFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TargetRepositoryMetaDataTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullName() throws Exception {
        new TargetRepositoryMetaData(1L, null, "fullName");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullFullName() throws Exception {
        new TargetRepositoryMetaData(1L, "name", null);
    }

    @Test
    public void getTest() throws Exception {
        TargetRepositoryMetaData result = new TargetRepositoryMetaData(1L, "name", "fullName");

        Assert.assertEquals(result.getId(), 1L);
        Assert.assertEquals(result.getName(), "name");
        Assert.assertEquals(result.getFullName(), "fullName");
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        TargetRepositoryMetaData result1 = new TargetRepositoryMetaData(1L, "name", "fullName");
        TargetRepositoryMetaData result2 = new TargetRepositoryMetaData(1L, "name", "fullName");

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        TargetRepositoryMetaData result = new TargetRepositoryMetaData(1L, "name", "fullName");

        Assert.assertFalse(result.equals(null));
    }

    @Test
    public void equalsDifferentClass() throws Exception {
        TargetRepositoryMetaData result = new TargetRepositoryMetaData(1L, "name", "fullName");

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        TargetRepositoryMetaData result = new TargetRepositoryMetaData(1L, "name", "fullName");

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        TargetRepositoryMetaData result1 = new TargetRepositoryMetaData(1L, "name1", "fullName");
        TargetRepositoryMetaData result2 = new TargetRepositoryMetaData(1L, "name2", "fullName");

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        TargetRepositoryMetaData result1 = new TargetRepositoryMetaData(1L, "name", "fullName");
        TargetRepositoryMetaData result2 = new TargetRepositoryMetaData(1L, "name", "fullName");

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        TargetRepositoryMetaData obj = new TargetRepositoryMetaData(1L, "name", "fullName");

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("id=1"));
        Assert.assertTrue(result.contains("name=name"));
        Assert.assertTrue(result.contains("fullName=fullName"));
    }

    @Test
    public void fromJson() throws Exception {
        String jsonString = ClasspathFileReader.readToString(TestFiles.TARGET_REPOSITORY_META_DATA);

        TargetRepositoryMetaData result = new ObjectMapper().readValue(jsonString, TargetRepositoryMetaData.class);

        Assert.assertEquals(result.getId(), 1296269);
        Assert.assertEquals(result.getName(), "Hello-World");
        Assert.assertEquals(result.getFullName(), "octocat/Hello-World");
    }

}
