package util;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.sql.SQLException;


public class ExceptionHandler {


    public static void handleSQLException(SQLException e, Component parent) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(parent,
            "Database Error:\n" + e.getMessage(),
            "DB Error", JOptionPane.ERROR_MESSAGE);
    }
    public static void handleValidationError(String message, Component parent) {
        JOptionPane.showMessageDialog(parent,
            message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }


    public static void handleGeneralException(Exception e, Component parent) {

        e.printStackTrace();
        JOptionPane.showMessageDialog(parent,
            "Unexpected Error:\n" + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }



    public static boolean validateNotEmpty(String value, String fieldName, Component parent) {
        if (value == null || value.trim().isEmpty()) {
            handleValidationError(fieldName + " khali nahi ho sakta.", parent);
            return false;
        }
        return true;
    }




    public static boolean validateNumeric(String value, String fieldName, Component parent) {
        try {
            Double.parseDouble(value.trim());
            return true;
        } catch (NumberFormatException e) {
            handleValidationError(fieldName + " mein sirf number hona chahiye.", parent);
            return false;
        }
    }



    public static boolean validatePhone(String phone, Component parent) {
        if (!phone.isEmpty() && !phone.matches("\\d{7,15}")) {
            handleValidationError("Phone number 7 se 15 digits ka hona chahiye.", parent);
            return false;
        }
        return true;
    }



    public static boolean validateEmail(String email, Component parent) {
        if (!email.isEmpty() && !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            handleValidationError("Email format galat hai.", parent);
            return false;
        }
        return true;
    }




    public static boolean validateSelection(int selectedId, Component parent) {
        if (selectedId < 0) {
            handleValidationError("Pehle table se koi record select karein.", parent);
            return false;
        }
        return true;
    }



    public static boolean confirmDelete(Component parent) {
        int result = JOptionPane.showConfirmDialog(parent,
            "Kya aap waqai delete karna chahte hain?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
}
