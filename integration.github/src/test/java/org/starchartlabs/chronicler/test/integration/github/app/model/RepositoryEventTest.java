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
import org.starchartlabs.chronicler.integration.github.app.model.RepositoryEvent;
import org.starchartlabs.chronicler.integration.github.app.model.RepositoryMetaData;
import org.starchartlabs.chronicler.test.integration.github.ClasspathFileReader;
import org.starchartlabs.chronicler.test.integration.github.TestFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RepositoryEventTest {

    private static final RepositoryMetaData REPOSITORY = new RepositoryMetaData(100L,
            "name",
            "fullname",
            false,
            "url",
            new OwnerMetaData(1000L, "login", "type", "owner-url"));

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullAction() throws Exception {
        new RepositoryEvent(null, REPOSITORY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullRepository() throws Exception {
        new RepositoryEvent("action", null);
    }

    @Test
    public void getTest() throws Exception {
        RepositoryEvent result = new RepositoryEvent("action", REPOSITORY);

        Assert.assertEquals(result.getAction(), "action");
        Assert.assertEquals(result.getRepository(), REPOSITORY);
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        RepositoryEvent result1 = new RepositoryEvent("action", REPOSITORY);
        RepositoryEvent result2 = new RepositoryEvent("action", REPOSITORY);

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        RepositoryEvent result = new RepositoryEvent("action", REPOSITORY);

        Assert.assertFalse(result.equals(null));
    }

    @Test
    public void equalsDifferentClass() throws Exception {
        RepositoryEvent result = new RepositoryEvent("action", REPOSITORY);

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        RepositoryEvent result = new RepositoryEvent("action", REPOSITORY);

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        RepositoryEvent result1 = new RepositoryEvent("action1", REPOSITORY);
        RepositoryEvent result2 = new RepositoryEvent("action2", REPOSITORY);

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        RepositoryEvent result1 = new RepositoryEvent("action", REPOSITORY);
        RepositoryEvent result2 = new RepositoryEvent("action", REPOSITORY);

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        RepositoryEvent obj = new RepositoryEvent("action", REPOSITORY);

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("action=action"));
        Assert.assertTrue(result.contains("repository=" + REPOSITORY.toString()));
    }

    @Test
    public void fromJson() throws Exception {
        String jsonString = ClasspathFileReader.readToString(TestFiles.REPOSITORY_EVENT);

        RepositoryEvent result = new ObjectMapper().readValue(jsonString, RepositoryEvent.class);

        Assert.assertEquals(result.getAction(), "created");

        RepositoryMetaData repository = result.getRepository();
        Assert.assertEquals(repository.getId(), 27496774);
        Assert.assertEquals(repository.getName(), "new-repository");
        Assert.assertEquals(repository.getFullName(), "baxterandthehackers/new-repository");
        Assert.assertEquals(repository.isPrivateRepository(), true);
        Assert.assertEquals(repository.getUrl(), "https://api.github.com/repos/baxterandthehackers/new-repository");

        OwnerMetaData owner = repository.getOwner();
        Assert.assertEquals(owner.getId(), 7649605L);
        Assert.assertEquals(owner.getLogin(), "baxterandthehackers");
        Assert.assertEquals(owner.getType(), "Organization");
        Assert.assertEquals(owner.getUrl(), "https://api.github.com/users/baxterandthehackers");
    }

}
