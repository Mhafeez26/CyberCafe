package util;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.sql.SQLException;


public class ExceptionHandler{


    public static void handleSQLException(SQLException e,Component parent){
        e.printStackTrace();
        JOptionPane.showMessageDialog(parent,
                "Database Error:\n"+e.getMessage(),
                "DB Error",JOptionPane.ERROR_MESSAGE);
   }


    public static void handleValidationError(String message,Component parent){
        JOptionPane.showMessageDialog(parent,
                message,"Validation Error",JOptionPane.WARNING_MESSAGE);
   }



    public static void handleGeneralException(Exception e,Component parent){
        e.printStackTrace();
        JOptionPane.showMessageDialog(parent,
                "Unexpected Error:\n"+e.getMessage(),
                "Error",JOptionPane.ERROR_MESSAGE);
   }

}