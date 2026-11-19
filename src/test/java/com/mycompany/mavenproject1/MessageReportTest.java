package com.mycompany.mavenproject1;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageReportTest{

    private MessageReport report;
    private Path tempJsonFile;

    private String msg2Hash;
    private String msg4ID;

    @BeforeEach
    void setUp() throws Exception {
        report = new MessageReport();
        tempJsonFile = Files.createTempFile("stored_messages_test", ".json");

        // Test Data Message 1 (Sent)
        Message msg1 = new Message("+27834557896", "Did you get the cake?");
        msg1.createMessageHash(1);
        report.addMessageRecord(new MessageReport.StoredEntry(
                "User", msg1.getRecipientCell(), msg1.getMessageText(),
                msg1.getMessageID(), msg1.getMessageHash(), "Sent"));

        // Test Data Message 2 (Stored)
        Message msg2 = new Message("+27838884567", "Where are you? You are late! I have asked you to be on time.");
        msg2.createMessageHash(2);
        msg2Hash = msg2.getMessageHash();
        report.saveStoredMessageToJson(msg2, "User", tempJsonFile);

        // Test Data Message 3 (Disregard)
        Message msg3 = new Message("+27834484567", "Yohoooo, I am at your gate.");
        msg3.createMessageHash(3);
        report.addMessageRecord(new MessageReport.StoredEntry(
                "User", msg3.getRecipientCell(), msg3.getMessageText(),
                msg3.getMessageID(), msg3.getMessageHash(), "Disregard"));

        // Test Data Message 4 (Sent, Developer 0838884567)
        Message msg4 = new Message("+27834557896", "It is dinner time !");
        msg4.createMessageHash(4);
        msg4ID = msg4.getMessageID();
        report.addMessageRecord(new MessageReport.StoredEntry(
                "0838884567", msg4.getRecipientCell(), msg4.getMessageText(),
                msg4.getMessageID(), msg4.getMessageHash(), "Sent"));

        // Test Data Message 5 (Stored)
        Message msg5 = new Message("+27838884567", "Ok, I am leaving without you.");
        msg5.createMessageHash(5);
        report.saveStoredMessageToJson(msg5, "User", tempJsonFile);

        report.populateStoredArrayFromJson(tempJsonFile);
    }

    @Test
    void testSentMessagesArrayPopulated() {
        List<String> actual = Arrays.asList(report.getSentMessages());
        List<String> expected = Arrays.asList("Did you get the cake?", "It is dinner time !");
        assertEquals(expected, actual);
    }

    @Test
    void testLongestStoredMessage() {
        String expected = "Where are you? You are late! I have asked you to be on time.";
        assertEquals(expected, report.findLongestMessage());
    }

    @Test
    void testSearchByMessageID() {
        String actual = report.searchMessageById(msg4ID);
        assertEquals("Recipient: +27834557896 | Message: \"It is dinner time !\"", actual);
    }

    @Test
    void testSearchMessagesByRecipient() {
        List<String> actual = report.searchByRecipient("+27838884567");
        List<String> expected = Arrays.asList(
                "Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you."
        );
        assertEquals(expected, actual);
    }

    @Test
    void testDeleteMessageByHash() {
        String actual = report.deleteByHash(msg2Hash);
        assertEquals(
                "Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.",
                actual
        );
    }

    @Test
    void testDisplayStoredMessagesReport() {
        String output = report.printFullStoredReport();
        assertTrue(output.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(output.contains("Ok, I am leaving without you."));
    }
}