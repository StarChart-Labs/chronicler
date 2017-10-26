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

import org.starchartlabs.chronicler.integration.github.app.model.InstallationMetaData;
import org.starchartlabs.chronicler.integration.github.app.model.OwnerMetaData;
import org.starchartlabs.chronicler.test.integration.github.ClasspathFileReader;
import org.starchartlabs.chronicler.test.integration.github.TestFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class InstallationMetaDataTest {

    private static final OwnerMetaData OWNER_META_DATA = new OwnerMetaData(1L, "login", "type", "url");

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullAccount() throws Exception {
        new InstallationMetaData(1L, null);
    }

    @Test
    public void getTest() throws Exception {
        InstallationMetaData result = new InstallationMetaData(1L, OWNER_META_DATA);

        Assert.assertEquals(result.getId(), 1L);
        Assert.assertEquals(result.getAccount(), OWNER_META_DATA);
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        InstallationMetaData result1 = new InstallationMetaData(1L, OWNER_META_DATA);
        InstallationMetaData result2 = new InstallationMetaData(1L, OWNER_META_DATA);

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        InstallationMetaData result = new InstallationMetaData(1L, OWNER_META_DATA);

        Assert.assertFalse(result.equals(null));
    }

    @Test
    public void equalsDifferentClass() throws Exception {
        InstallationMetaData result = new InstallationMetaData(1L, OWNER_META_DATA);

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        InstallationMetaData result = new InstallationMetaData(1L, OWNER_META_DATA);

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        InstallationMetaData result1 = new InstallationMetaData(1L, OWNER_META_DATA);
        InstallationMetaData result2 = new InstallationMetaData(2L, OWNER_META_DATA);

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        InstallationMetaData result1 = new InstallationMetaData(1L, OWNER_META_DATA);
        InstallationMetaData result2 = new InstallationMetaData(1L, OWNER_META_DATA);

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        InstallationMetaData obj = new InstallationMetaData(1L, OWNER_META_DATA);

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("id=1"));
        Assert.assertTrue(result.contains("account=" + OWNER_META_DATA.toString()));
    }

    @Test
    public void fromJson() throws Exception {
        String jsonString = ClasspathFileReader.readToString(TestFiles.INSTALLATION_META_DATA);

        InstallationMetaData result = new ObjectMapper().readValue(jsonString, InstallationMetaData.class);

        Assert.assertEquals(result.getId(), 2L);
        Assert.assertEquals(result.getAccount().getId(), 1L);
        Assert.assertEquals(result.getAccount().getLogin(), "octocat");
        Assert.assertEquals(result.getAccount().getType(), "User");
        Assert.assertEquals(result.getAccount().getUrl(), "https://api.github.com/users/octocat");
    }

}
