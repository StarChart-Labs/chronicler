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

import org.starchartlabs.chronicler.integration.github.app.model.PingEvent;
import org.starchartlabs.chronicler.test.integration.github.ClasspathFileReader;
import org.starchartlabs.chronicler.test.integration.github.TestFiles;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PingEventTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullZen() throws Exception {
        new PingEvent(null);
    }

    @Test
    public void getTest() throws Exception {
        PingEvent result = new PingEvent("zen");

        Assert.assertEquals(result.getZen(), "zen");
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        PingEvent result1 = new PingEvent("zen");
        PingEvent result2 = new PingEvent("zen");

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        PingEvent result = new PingEvent("zen");

        Assert.assertFalse(result.equals(null));
    }

    @Test
    public void equalsDifferentClass() throws Exception {
        PingEvent result = new PingEvent("zen");

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        PingEvent result = new PingEvent("zen");

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        PingEvent result1 = new PingEvent("zen1");
        PingEvent result2 = new PingEvent("zen2");

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        PingEvent result1 = new PingEvent("zen");
        PingEvent result2 = new PingEvent("zen");

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        PingEvent obj = new PingEvent("zen");

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("zen=zen"));
    }

    @Test
    public void fromJson() throws Exception {
        String jsonString = ClasspathFileReader.readToString(TestFiles.PING_EVENT);

        PingEvent result = new ObjectMapper().readValue(jsonString, PingEvent.class);

        Assert.assertEquals(result.getZen(), "GitHub zen");
    }

}
