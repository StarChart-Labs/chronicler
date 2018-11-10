/*
 * Copyright (c) Nov 9, 2018 StarChart Labs Authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    romeara - initial API and implementation and/or initial documentation
 */
package org.starchartlabs.chronicler.test.machete;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import org.mockito.Mockito;
import org.starchartlabs.chronicler.machete.SnsEvents;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNS;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;

public class SnsEventsTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void toMessagesNullSnsEvent() throws Exception {
        SnsEvents.getMessages(null, Function.identity(), "subject");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void toMessagesNulltoEvent() throws Exception {
        SNSEvent snsEvent = Mockito.mock(SNSEvent.class);

        SnsEvents.getMessages(snsEvent, null, "subject");
    }

    @Test
    public void toMessagesNullSubject() throws Exception {
        SNSRecord record1 = createRecordMock("subject1", "message1");
        SNSRecord record2 = createRecordMock("subject2", "message2");

        SNSEvent snsEvent = Mockito.mock(SNSEvent.class);
        Mockito.when(snsEvent.getRecords()).thenReturn(Arrays.asList(record1, record2));

        Collection<String> result = SnsEvents.getMessages(snsEvent, Function.identity(), null);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 2);
        Assert.assertTrue(result.contains(record1.getSNS().getMessage()));
        Assert.assertTrue(result.contains(record2.getSNS().getMessage()));
    }

    @Test
    public void toMessages() throws Exception {
        SNSRecord record1 = createRecordMock("subject1", "message1");
        SNSRecord record2 = createRecordMock("subject2", "message2");

        SNSEvent snsEvent = Mockito.mock(SNSEvent.class);
        Mockito.when(snsEvent.getRecords()).thenReturn(Arrays.asList(record1, record2));

        Collection<String> result = SnsEvents.getMessages(snsEvent, Function.identity(), record1.getSNS().getSubject());

        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains(record1.getSNS().getMessage()));
    }

    private SNSRecord createRecordMock(String subject, String message) {
        SNS sns = Mockito.mock(SNS.class);
        Mockito.when(sns.getSubject()).thenReturn(subject);
        Mockito.when(sns.getMessage()).thenReturn(message);

        SNSRecord record = Mockito.mock(SNSRecord.class);
        Mockito.when(record.getSNS()).thenReturn(sns);

        return record;
    }

}
