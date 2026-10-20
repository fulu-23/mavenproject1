package com.mycompany.mavenproject1;

import java.util.Scanner;

public class Mavenproject1 {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Login userLogin = new Login();

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

        if (registrationMessage.equals("Username and password successfully captured.")) {
            System.out.println("\n=== Login ===");
            System.out.print("Enter username: ");
            String loginUser = input.nextLine();

            System.out.print("Enter password: ");
            String loginPass = input.nextLine();

            boolean loggedIn = userLogin.loginUser(loginUser, loginPass);
            System.out.println(userLogin.returnLoginStatus(loggedIn));

            if (loggedIn) {
                System.out.println("\nWelcome to QuickChat.");

                boolean quit = false;
                while (!quit) {
                    System.out.println("\nSelect an option:");
                    System.out.println("1) Send Messages");
                    System.out.println("2) Show recently sent messages");
                    System.out.println("3) Quit");
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

                                msg.createMessageHash(i);

                                System.out.println("\nChoose an action:");
                                System.out.println("1) Send Message");
                                System.out.println("2) Disregard Message");
                                System.out.println("3) Store Message to send later");
                                System.out.print("Choice: ");
                                int sendChoice = Integer.parseInt(input.nextLine());

                                String status = msg.SentMessage(sendChoice);
                                System.out.println(status);

                                System.out.println("\n--- Message Summary ---");
                                System.out.println("Message ID: " + msg.getMessageID());
                                System.out.println("Message Hash: " + msg.getMessageHash());
                                System.out.println("Recipient: " + msg.getRecipientCell());
                                System.out.println("Message: " + msg.getMessageText());
                            }

                            System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
                            break;

                        case 2:
                            System.out.println("Coming Soon.");
                            break;

                        case 3:
                            quit = true;
                            System.out.println("Goodbye!");
                            break;

                        default:
                            System.out.println("Invalid menu option.");
                    }
                }
            }
        }
        input.close();
    }
}