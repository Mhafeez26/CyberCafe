package db;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection{

    private static final String URL="jdbc:mysql://localhost:3306/cybercafe";
    private static final String USER="root";
    private static final String PASSWORD="";

    private static Connection connection=null;

    public static Connection getConnection(){
        try{
            connection=DriverManager.getConnection(URL,USER,PASSWORD);
            System.out.println("Connected to database successfully");
            return connection;
      } catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Database connection failed: "+e.getMessage(),
                "DB Error",JOptionPane.ERROR_MESSAGE);
      }
        return connection;
  }
}
