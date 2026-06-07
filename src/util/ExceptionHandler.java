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
}