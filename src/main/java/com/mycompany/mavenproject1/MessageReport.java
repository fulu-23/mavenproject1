package com.mycompany.mavenproject1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageReport {

    public static class StoredEntry {
        private final String sender;
        private final String recipient;
        private final String messageText;
        private final String messageID;
        private final String messageHash;
        private final String flag;

        public StoredEntry(String sender, String recipient, String messageText,
                           String messageID, String messageHash, String flag) {
            this.sender = sender;
            this.recipient = recipient;
            this.messageText = messageText;
            this.messageID = messageID;
            this.messageHash = messageHash;
            this.flag = flag;
        }

        public String getSender() {
            return sender;
        }

        public String getRecipient() {
            return recipient;
        }

        public String getMessageText() {
            return messageText;
        }

        public String getMessageID() {
            return messageID;
        }

        public String getMessageHash() {
            return messageHash;
        }

        public String getFlag() {
            return flag;
        }
    }

    private static final Path JSON_FILE = Paths.get("stored_messages.json");

    private final List<StoredEntry> allEntries = new ArrayList<>();
    private final List<String> sentMessagesList = new ArrayList<>();
    private final List<String> disregardedMessagesList = new ArrayList<>();
    private final List<StoredEntry> storedMessagesList = new ArrayList<>();
    private final List<String> messageHashesList = new ArrayList<>();
    private final List<String> messageIDsList = new ArrayList<>();

    public void addMessageRecord(StoredEntry entry) {
        allEntries.add(entry);

        if (entry.getMessageID() != null && !entry.getMessageID().isEmpty()) {
            messageIDsList.add(entry.getMessageID());
        }
        if (entry.getMessageHash() != null && !entry.getMessageHash().isEmpty()) {
            messageHashesList.add(entry.getMessageHash());
        }

        if ("Sent".equalsIgnoreCase(entry.getFlag())) {
            sentMessagesList.add(entry.getMessageText());
        } else if ("Disregard".equalsIgnoreCase(entry.getFlag())) {
            disregardedMessagesList.add(entry.getMessageText());
        } else if ("Stored".equalsIgnoreCase(entry.getFlag())) {
            storedMessagesList.add(entry);
        }
    }

    public void saveStoredMessageToJson(Message msg, String sender) throws IOException {
        saveStoredMessageToJson(msg, sender, JSON_FILE);
    }

    public void saveStoredMessageToJson(Message msg, String sender, Path filePath) throws IOException {
        List<StoredEntry> currentList = readJsonFile(filePath);
        currentList.add(new StoredEntry(
                sender,
                msg.getRecipientCell(),
                msg.getMessageText(),
                msg.getMessageID(),
                msg.getMessageHash(),
                "Stored"
        ));
        writeJsonFile(currentList, filePath);
    }

    public void writeJsonFile(List<StoredEntry> list, Path filePath) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        for (int i = 0; i < list.size(); i++) {
            StoredEntry item = list.get(i);
            json.append("  {\n");
            json.append("    \"sender\": \"").append(escape(item.getSender())).append("\",\n");
            json.append("    \"recipientCell\": \"").append(escape(item.getRecipient())).append("\",\n");
            json.append("    \"messageID\": \"").append(escape(item.getMessageID())).append("\",\n");
            json.append("    \"messageHash\": \"").append(escape(item.getMessageHash())).append("\",\n");
            json.append("    \"messageText\": \"").append(escape(item.getMessageText())).append("\"\n");
            json.append("  }");
            if (i < list.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]\n");
        Files.write(filePath, json.toString().getBytes(StandardCharsets.UTF_8));
    }

    public List<StoredEntry> readJsonFile() throws IOException {
        return readJsonFile(JSON_FILE);
    }

    public List<StoredEntry> readJsonFile(Path filePath) throws IOException {
        List<StoredEntry> loaded = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return loaded;
        }
        String text = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8).trim();
        if (text.isEmpty() || "[]".equals(text)) {
            return loaded;
        }

        String[] parts = text.substring(1, text.length() - 1).split("\\},\\s*\\{");
        for (String part : parts) {
            String obj = part.trim();
            if (!obj.startsWith("{")) {
                obj = "{" + obj;
            }
            if (!obj.endsWith("}")) {
                obj = obj + "}";
            }

            String sender = getJsonValue(obj, "sender");
            String recipient = getJsonValue(obj, "recipientCell");
            String id = getJsonValue(obj, "messageID");
            String hash = getJsonValue(obj, "messageHash");
            String msgBody = getJsonValue(obj, "messageText");

            loaded.add(new StoredEntry(
                    sender.isEmpty() ? "User" : sender,
                    recipient,
                    msgBody,
                    id,
                    hash,
                    "Stored"
            ));
        }
        return loaded;
    }

    public void populateStoredArrayFromJson() throws IOException {
        populateStoredArrayFromJson(JSON_FILE);
    }

    public void populateStoredArrayFromJson(Path filePath) throws IOException {
        List<StoredEntry> loaded = readJsonFile(filePath);
        storedMessagesList.clear();
        for (StoredEntry item : loaded) {
            storedMessagesList.add(item);
            if (item.getMessageID() != null && !item.getMessageID().isEmpty()
                    && !messageIDsList.contains(item.getMessageID())) {
                messageIDsList.add(item.getMessageID());
            }
            if (item.getMessageHash() != null && !item.getMessageHash().isEmpty()
                    && !messageHashesList.contains(item.getMessageHash())) {
                messageHashesList.add(item.getMessageHash());
            }
            boolean existsInAll = false;
            for (StoredEntry existing : allEntries) {
                if (existing.getMessageID() != null && existing.getMessageID().equals(item.getMessageID())) {
                    existsInAll = true;
                    break;
                }
            }
            if (!existsInAll) {
                allEntries.add(item);
            }
        }
    }

    // a. Display sender and recipient of all stored messages
    public String showSenderAndRecipient() {
        if (storedMessagesList.isEmpty()) {
            return "No stored messages found.";
        }
        StringBuilder out = new StringBuilder("--- Sender and Recipient of Stored Messages ---\n");
        for (int i = 0; i < storedMessagesList.size(); i++) {
            StoredEntry item = storedMessagesList.get(i);
            out.append((i + 1)).append(". Sender: ").append(item.getSender())
               .append(" | Recipient: ").append(item.getRecipient()).append("\n");
        }
        return out.toString().trim();
    }

    // b. Display the longest stored message
    public String findLongestMessage() {
        StoredEntry longest = null;
        for (StoredEntry item : storedMessagesList) {
            if (longest == null || item.getMessageText().length() > longest.getMessageText().length()) {
                longest = item;
            }
        }
        if (longest == null) {
            for (StoredEntry item : allEntries) {
                if (longest == null || item.getMessageText().length() > longest.getMessageText().length()) {
                    longest = item;
                }
            }
        }
        return longest == null ? "" : longest.getMessageText();
    }

    // c. Search for a message ID and display the corresponding recipient and message
    public String searchMessageById(String idToFind) {
        for (StoredEntry item : allEntries) {
            if (idToFind != null && idToFind.equals(item.getMessageID())) {
                return "Recipient: " + item.getRecipient() + " | Message: \"" + item.getMessageText() + "\"";
            }
        }
        return "Message ID not found.";
    }

    // d. Search for all messages (sent or stored) for a particular recipient
    public List<String> searchByRecipient(String recipientNumber) {
        List<String> results = new ArrayList<>();
        for (StoredEntry item : allEntries) {
            boolean sentOrStored = "Sent".equalsIgnoreCase(item.getFlag())
                    || "Stored".equalsIgnoreCase(item.getFlag());
            if (sentOrStored && recipientNumber != null && recipientNumber.equals(item.getRecipient())) {
                results.add(item.getMessageText());
            }
        }
        return results;
    }

    // e. Delete a message using the message hash
    public String deleteByHash(String targetHash) {
        for (int i = 0; i < allEntries.size(); i++) {
            StoredEntry item = allEntries.get(i);
            if (targetHash != null && targetHash.equals(item.getMessageHash())) {
                allEntries.remove(i);
                storedMessagesList.remove(item);
                sentMessagesList.remove(item.getMessageText());
                disregardedMessagesList.remove(item.getMessageText());
                messageHashesList.remove(item.getMessageHash());
                messageIDsList.remove(item.getMessageID());
                return "Message: \"" + item.getMessageText() + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }

    // f. Display a report that lists the full details of all stored messages
    public String printFullStoredReport() {
        if (storedMessagesList.isEmpty()) {
            return "=== Stored Messages Report ===\nNo stored messages found.";
        }
        StringBuilder out = new StringBuilder("=== Stored Messages Report ===\n");
        for (int i = 0; i < storedMessagesList.size(); i++) {
            StoredEntry item = storedMessagesList.get(i);
            out.append("Message ").append(i + 1).append(":\n")
               .append("  Message ID   : ").append(item.getMessageID()).append("\n")
               .append("  Message Hash : ").append(item.getMessageHash()).append("\n")
               .append("  Sender       : ").append(item.getSender()).append("\n")
               .append("  Recipient    : ").append(item.getRecipient()).append("\n")
               .append("  Message      : \"").append(item.getMessageText()).append("\"\n");
        }
        return out.toString().trim();
    }

    public String[] getSentMessages() {
        return sentMessagesList.toArray(new String[0]);
    }

    public String[] getDisregardedMessages() {
        return disregardedMessagesList.toArray(new String[0]);
    }

    public StoredEntry[] getStoredMessages() {
        return storedMessagesList.toArray(new StoredEntry[0]);
    }

    public String[] getMessageHashes() {
        return messageHashesList.toArray(new String[0]);
    }

    public String[] getMessageIDs() {
        return messageIDsList.toArray(new String[0]);
    }

    private String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private String unescape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\\\", "\\");
    }

    private String getJsonValue(String jsonObj, String key) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"");
        Matcher matcher = pattern.matcher(jsonObj);
        if (matcher.find()) {
            return unescape(matcher.group(1));
        }
        return "";
    }
}
