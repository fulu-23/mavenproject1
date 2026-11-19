package com.mycompany.mavenproject1;

import java.util.Scanner;

public class Mavenproject1 {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Login userLogin = new Login();
        MessageReport report = new MessageReport();

        System.out.println("=== Registration ===");
        System.out.print("Enter First Name: ");
        userLogin.setFirstName(input.nextLine());

        System.out.print("Enter Last Name: ");
        userLogin.setLastName(input.nextLine());

        System.out.print("Enter Username: ");
        userLogin.setUserName(input.nextLine());

        System.out.print("Enter Password: ");
        userLogin.setPassword(input.nextLine());

        System.out.print("Enter Cell Phone Number (+27...): ");
        userLogin.setCellPhoneNumber(input.nextLine());

        String registrationMessage = userLogin.registerUser();
        System.out.println(registrationMessage);

        System.out.println("\n=== Login ===");
        System.out.print("Enter username: ");
        String loginUser = input.nextLine();

        System.out.print("Enter password: ");
        String loginPass = input.nextLine();

        boolean loggedIn = userLogin.loginUser(loginUser, loginPass);
        System.out.println(userLogin.returnLoginStatus(loggedIn));

        if (loggedIn) {
            System.out.println("\nWelcome to QuickChat.");
            String senderName = userLogin.getFirstName() + " " + userLogin.getLastName();

            try {
                report.populateStoredArrayFromJson();
            } catch (Exception e) {
                System.out.println("No existing stored messages JSON yet.");
            }

            boolean quit = false;
            while (!quit) {
                System.out.println("\nSelect an option:");
                System.out.println("1) Send Messages");
                System.out.println("2) Show recently sent messages");
                System.out.println("3) Quit");
                System.out.println("4) Stored Messages");
                System.out.print("Choice: ");

                int option = Integer.parseInt(input.nextLine());

                switch (option) {
                    case 1:
                        System.out.print("How many messages would you like to enter? ");
                        int numMessages = Integer.parseInt(input.nextLine());

                        for (int i = 0; i < numMessages; i++) {
                            System.out.println("\n--- Message " + (i + 1) + " ---");
                            System.out.print("Enter Recipient Number: ");
                            String recipient = input.nextLine();

                            System.out.print("Enter Message: ");
                            String msgText = input.nextLine();

                            Message msg = new Message(recipient, msgText);

                            String lengthValidation = msg.validateMessageLength();
                            System.out.println(lengthValidation);

                            if (lengthValidation.startsWith("Message exceeds")) {
                                continue;
                            }

                            String hash = msg.createMessageHash(i);

                            System.out.println("\nChoose an action:");
                            System.out.println("1) Send Message");
                            System.out.println("2) Disregard Message");
                            System.out.println("3) Store Message to send later");
                            System.out.print("Choice: ");
                            int sendChoice = Integer.parseInt(input.nextLine());

                            String status = msg.SentMessage(sendChoice);
                            System.out.println(status);

                            String flag;
                            if (sendChoice == 1) {
                                flag = "Sent";
                            } else if (sendChoice == 2) {
                                flag = "Disregard";
                            } else if (sendChoice == 3) {
                                flag = "Stored";
                                try {
                                    report.saveStoredMessageToJson(msg, senderName);
                                    report.populateStoredArrayFromJson();
                                } catch (Exception e) {
                                    System.out.println("Could not store message in JSON: " + e.getMessage());
                                }
                            } else {
                                continue;
                            }

                            report.addMessageRecord(new MessageReport.StoredEntry(
                                    senderName,
                                    msg.getRecipientCell(),
                                    msg.getMessageText(),
                                    msg.getMessageID(),
                                    hash,
                                    flag
                            ));

                            System.out.println("\n--- Message Summary ---");
                            System.out.println("Message ID: " + msg.getMessageID());
                            System.out.println("Message Hash: " + hash);
                            System.out.println("Recipient: " + msg.getRecipientCell());
                            System.out.println("Message: " + msg.getMessageText());
                        }

                        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
                        break;

                    case 2:
                        String[] sent = report.getSentMessages();
                        if (sent.length == 0) {
                            System.out.println("No recently sent messages.");
                        } else {
                            System.out.println("\n--- Recently Sent Messages ---");
                            for (int i = 0; i < sent.length; i++) {
                                System.out.println((i + 1) + ". " + sent[i]);
                            }
                        }
                        break;

                    case 3:
                        quit = true;
                        System.out.println("Goodbye!");
                        break;

                    case 4:
                        try {
                            report.populateStoredArrayFromJson();
                        } catch (Exception e) {
                            System.out.println("Error loading stored messages JSON: " + e.getMessage());
                        }

                        System.out.println("\n--- Stored Messages ---");
                        System.out.println("a. Display sender and recipient of all stored messages");
                        System.out.println("b. Display the longest stored message");
                        System.out.println("c. Search for a message ID");
                        System.out.println("d. Search messages by recipient");
                        System.out.println("e. Delete a message using message hash");
                        System.out.println("f. Display report of all stored messages");
                        System.out.print("Choose an option: ");
                        String choice = input.nextLine().trim().toLowerCase();

                        switch (choice) {
                            case "a":
                                System.out.println(report.showSenderAndRecipient());
                                break;
                            case "b":
                                System.out.println("Longest message: \"" + report.findLongestMessage() + "\"");
                                break;
                            case "c":
                                System.out.print("Enter Message ID: ");
                                String id = input.nextLine().trim();
                                System.out.println(report.searchMessageById(id));
                                break;
                            case "d":
                                System.out.print("Enter Recipient: ");
                                String rec = input.nextLine().trim();
                                System.out.println(report.searchByRecipient(rec));
                                break;
                            case "e":
                                System.out.print("Enter Message Hash: ");
                                String hash = input.nextLine().trim();
                                System.out.println(report.deleteByHash(hash));
                                break;
                            case "f":
                                System.out.println(report.printFullStoredReport());
                                break;
                            default:
                                System.out.println("Invalid option.");
                                break;
                        }
                        break;

                    default:
                        System.out.println("Invalid menu option.");
                        break;
                }
            }
        }

        input.close();
    }
}