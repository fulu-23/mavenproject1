package com.mycompany.mavenproject1;

import java.util.regex.Pattern;

public class Login {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellPhoneNumber;

    public Login() {}

    public Login(String firstName, String lastName, String username, String password, String cellPhoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public boolean checkUserName(String username) {
        if (username == null) return false;
        return username.contains("_") && username.length() <= 5;
    }

    public boolean checkPasswordComplexity(String password) {
        if (password == null) return false;
        boolean hasMinLength = password.length() >= 8;
        boolean hasCapital = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasNumber = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]").matcher(password).find();
        return hasMinLength && hasCapital && hasNumber && hasSpecial;
    }

    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        if (cellPhoneNumber == null) return false;
        String regex = "^\\+27[0-9]{9}$";
        return Pattern.matches(regex, cellPhoneNumber);
    }

    public String registerUser() {
        if (!checkUserName(this.username)) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than 5 characters in length.";
        }
        if (!checkPasswordComplexity(this.password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least 8 characters, a capital letter, a number, and a special character.";
        }
        if (!checkCellPhoneNumber(this.cellPhoneNumber)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }
        return "Username and password successfully captured.";
    }

    public boolean loginUser(String enteredUser, String enteredPass) {
        if (enteredUser == null || enteredPass == null) return false;
        return enteredUser.equals(this.username) && enteredPass.equals(this.password);
    }

    public String returnLoginStatus(boolean isLoggedIn) {
        if (isLoggedIn) {
            return "Welcome " + this.firstName + ", " + this.lastName + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUserName(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getCellPhoneNumber() { return cellPhoneNumber; }
    public void setCellPhoneNumber(String cellPhoneNumber) { this.cellPhoneNumber = cellPhoneNumber; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}