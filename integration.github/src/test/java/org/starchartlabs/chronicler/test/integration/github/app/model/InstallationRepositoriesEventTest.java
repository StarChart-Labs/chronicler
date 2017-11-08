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

import java.util.Collections;
import java.util.List;

import org.starchartlabs.chronicler.integration.github.app.model.InstallationMetaData;
import org.starchartlabs.chronicler.integration.github.app.model.InstallationRepositoriesEvent;
import org.starchartlabs.chronicler.integration.github.app.model.OwnerMetaData;
import org.starchartlabs.chronicler.integration.github.app.model.TargetRepositoryMetaData;
import org.starchartlabs.chronicler.test.integration.github.ClasspathFileReader;
import org.starchartlabs.chronicler.test.integration.github.TestFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class InstallationRepositoriesEventTest {

    private static final InstallationMetaData INSTALLATION = new InstallationMetaData(100L,
            new OwnerMetaData(200L, "login", "type", "url"));

    private static final List<TargetRepositoryMetaData> ADDED = Collections.singletonList(
            new TargetRepositoryMetaData(1000L, "added", "owner/added"));

    private static final List<TargetRepositoryMetaData> REMOVED = Collections.singletonList(
            new TargetRepositoryMetaData(2000L, "removed", "owner/removed"));

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullAction() throws Exception {
        new InstallationRepositoriesEvent(null, INSTALLATION, "repositorySelection", ADDED, REMOVED);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullInstallation() throws Exception {
        new InstallationRepositoriesEvent("action", null, "repositorySelection", ADDED, REMOVED);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullRepositorySelection() throws Exception {
        new InstallationRepositoriesEvent("action", INSTALLATION, null, ADDED, REMOVED);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullRepositoriesAdded() throws Exception {
        new InstallationRepositoriesEvent("action", INSTALLATION, "repositorySelection", null, REMOVED);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullRepositoriesRemoved() throws Exception {
        new InstallationRepositoriesEvent("action", INSTALLATION, "repositorySelection", ADDED, null);
    }

    @Test
    public void getTest() throws Exception {
        InstallationRepositoriesEvent result = new InstallationRepositoriesEvent("action", INSTALLATION,
                "repositorySelection", ADDED, REMOVED);

        Assert.assertEquals(result.getAction(), "action");
        Assert.assertEquals(result.getInstallation(), INSTALLATION);
        Assert.assertEquals(result.getRepositorySelection(), "repositorySelection");
        Assert.assertEquals(result.getRepositoriesAdded(), ADDED);
        Assert.assertEquals(result.getRepositoriesRemoved(), REMOVED);
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        InstallationRepositoriesEvent result1 = new InstallationRepositoriesEvent("action", INSTALLATION,
                "repositorySelection", ADDED, REMOVED);
        InstallationRepositoriesEvent result2 = new InstallationRepositoriesEvent("action", INSTALLATION,
                "repositorySelection", ADDED, REMOVED);

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        InstallationRepositoriesEvent result = new InstallationRepositoriesEvent("action", INSTALLATION,
                "repositorySelection", ADDED, REMOVED);

        Assert.assertFalse(result.equals(null));
    }

    @Test
    public void equalsDifferentClass() throws Exception {
        InstallationRepositoriesEvent result = new InstallationRepositoriesEvent("action", INSTALLATION,
                "repositorySelection", ADDED, REMOVED);

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        InstallationRepositoriesEvent result = new InstallationRepositoriesEvent("action", INSTALLATION,
                "repositorySelection", ADDED, REMOVED);

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        InstallationRepositoriesEvent result1 = new InstallationRepositoriesEvent("action1", INSTALLATION,
                "repositorySelection", ADDED, REMOVED);
        InstallationRepositoriesEvent result2 = new InstallationRepositoriesEvent("action2", INSTALLATION,
                "repositorySelection", ADDED, REMOVED);

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        InstallationRepositoriesEvent result1 = new InstallationRepositoriesEvent("action", INSTALLATION,
                "repositorySelection", ADDED, REMOVED);
        InstallationRepositoriesEvent result2 = new InstallationRepositoriesEvent("action", INSTALLATION,
                "repositorySelection", ADDED, REMOVED);

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        InstallationRepositoriesEvent obj = new InstallationRepositoriesEvent("action", INSTALLATION,
                "repositorySelection", ADDED, REMOVED);

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("action=action"));
        Assert.assertTrue(result.contains("installation=" + INSTALLATION.toString()));
        Assert.assertTrue(result.contains("repositorySelection=repositorySelection"));
        Assert.assertTrue(result.contains("repositoriesAdded=" + ADDED.toString()));
        Assert.assertTrue(result.contains("repositoriesRemoved=" + REMOVED.toString()));
    }

    @Test
    public void fromJson() throws Exception {
        String jsonString = ClasspathFileReader.readToString(TestFiles.INSTALLATION_REPOSITORIES_EVENT);

        InstallationRepositoriesEvent result = new ObjectMapper().readValue(jsonString,
                InstallationRepositoriesEvent.class);

        Assert.assertEquals(result.getAction(), "removed");
        Assert.assertEquals(result.getInstallation().getId(), 2L);
        Assert.assertEquals(result.getInstallation().getAccount().getId(), 1L);
        Assert.assertEquals(result.getInstallation().getAccount().getLogin(), "octocat");
        Assert.assertEquals(result.getInstallation().getAccount().getType(), "User");
        Assert.assertEquals(result.getInstallation().getAccount().getUrl(), "https://api.github.com/users/octocat");
        Assert.assertEquals(result.getRepositorySelection(), "selected");

        Assert.assertEquals(result.getRepositoriesAdded().size(), 0);
        Assert.assertEquals(result.getRepositoriesRemoved().size(), 1);

        Assert.assertEquals(result.getRepositoriesRemoved().get(0),
                new TargetRepositoryMetaData(1296269L, "Hello-World", "octocat/Hello-World"));
    }

}
