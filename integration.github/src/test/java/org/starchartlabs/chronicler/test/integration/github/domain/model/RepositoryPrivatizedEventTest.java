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

import org.starchartlabs.chronicler.integration.github.domain.model.RepositoryPrivatizedEvent;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RepositoryPrivatizedEventTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullOwnerName() throws Exception {
        new RepositoryPrivatizedEvent(1L, null, "repositoryName");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullRepositoryName() throws Exception {
        new RepositoryPrivatizedEvent(1L, "ownerName", null);
    }

    @Test
    public void getTest() throws Exception {
        RepositoryPrivatizedEvent result = new RepositoryPrivatizedEvent(1L, "ownerName", "repositoryName");

        Assert.assertEquals(result.getId(), 1L);
        Assert.assertEquals(result.getOwnerName(), "ownerName");
        Assert.assertEquals(result.getRepositoryName(), "repositoryName");
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        RepositoryPrivatizedEvent result1 = new RepositoryPrivatizedEvent(1L, "ownerName", "repositoryName");
        RepositoryPrivatizedEvent result2 = new RepositoryPrivatizedEvent(1L, "ownerName", "repositoryName");

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        RepositoryPrivatizedEvent result = new RepositoryPrivatizedEvent(1L, "ownerName", "repositoryName");

        Assert.assertFalse(result.equals(null));
    }

    @Test
    public void equalsDifferentClass() throws Exception {
        RepositoryPrivatizedEvent result = new RepositoryPrivatizedEvent(1L, "ownerName", "repositoryName");

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        RepositoryPrivatizedEvent result = new RepositoryPrivatizedEvent(1L, "ownerName", "repositoryName");

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        RepositoryPrivatizedEvent result1 = new RepositoryPrivatizedEvent(1L, "ownerName1", "repositoryName");
        RepositoryPrivatizedEvent result2 = new RepositoryPrivatizedEvent(1L, "ownerName2", "repositoryName");

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        RepositoryPrivatizedEvent result1 = new RepositoryPrivatizedEvent(1L, "ownerName", "repositoryName");
        RepositoryPrivatizedEvent result2 = new RepositoryPrivatizedEvent(1L, "ownerName", "repositoryName");

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        RepositoryPrivatizedEvent obj = new RepositoryPrivatizedEvent(1L, "ownerName", "repositoryName");

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("id=1"));
        Assert.assertTrue(result.contains("ownerName=ownerName"));
        Assert.assertTrue(result.contains("repositoryName=repositoryName"));
    }

}
