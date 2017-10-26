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

import org.starchartlabs.chronicler.integration.github.app.model.InstallationEvent;
import org.starchartlabs.chronicler.integration.github.app.model.InstallationMetaData;
import org.starchartlabs.chronicler.integration.github.app.model.OwnerMetaData;
import org.starchartlabs.chronicler.test.integration.github.ClasspathFileReader;
import org.starchartlabs.chronicler.test.integration.github.TestFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class InstallationEventTest {

    private static final InstallationMetaData INSTALLATION = new InstallationMetaData(2L,
            new OwnerMetaData(1L, "login", "type", "ownerUrl"));

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullAction() throws Exception {
        new InstallationEvent(null, INSTALLATION);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullInstallation() throws Exception {
        new InstallationEvent("action", null);
    }

    @Test
    public void getTest() throws Exception {
        InstallationEvent result = new InstallationEvent("action", INSTALLATION);

        Assert.assertEquals(result.getAction(), "action");
        Assert.assertEquals(result.getInstallation(), INSTALLATION);
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        InstallationEvent result1 = new InstallationEvent("action", INSTALLATION);
        InstallationEvent result2 = new InstallationEvent("action", INSTALLATION);

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        InstallationEvent result = new InstallationEvent("action", INSTALLATION);

        Assert.assertFalse(result.equals(null));
    }

    @Test
    public void equalsDifferentClass() throws Exception {
        InstallationEvent result = new InstallationEvent("action", INSTALLATION);

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        InstallationEvent result = new InstallationEvent("action", INSTALLATION);

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        InstallationEvent result1 = new InstallationEvent("action1", INSTALLATION);
        InstallationEvent result2 = new InstallationEvent("action2", INSTALLATION);

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        InstallationEvent result1 = new InstallationEvent("action", INSTALLATION);
        InstallationEvent result2 = new InstallationEvent("action", INSTALLATION);

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        InstallationEvent obj = new InstallationEvent("action", INSTALLATION);

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("action=action"));
        Assert.assertTrue(result.contains("installation=" + INSTALLATION.toString()));
    }

    @Test
    public void fromJson() throws Exception {
        String jsonString = ClasspathFileReader.readToString(TestFiles.INSTALLATION_EVENT);

        InstallationEvent result = new ObjectMapper().readValue(jsonString, InstallationEvent.class);

        Assert.assertEquals(result.getAction(), "deleted");
        Assert.assertEquals(result.getInstallation().getId(), 2L);
        Assert.assertEquals(result.getInstallation().getAccount().getId(), 1L);
        Assert.assertEquals(result.getInstallation().getAccount().getLogin(), "octocat");
        Assert.assertEquals(result.getInstallation().getAccount().getType(), "User");
        Assert.assertEquals(result.getInstallation().getAccount().getUrl(), "https://api.github.com/users/octocat");
    }

}
