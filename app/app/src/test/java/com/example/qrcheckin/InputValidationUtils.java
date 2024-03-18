package com.example.qrcheckin;

public class InputValidationUtils {
    public static boolean isNameValid(String name) {
        return name != null && !name.isEmpty() && name.length() <= 200;
    }

    public static boolean isPhoneValid(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }

    public static boolean isEmailValid(String email) {
        return email != null && email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+");
    }


}
