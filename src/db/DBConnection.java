package db;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBConnection{

    private static final String URL="jdbc:mysql://localhost:3306/cybercafe";
    private static final String USER="root";
    private static final String PASSWORD="";

    //singleton patterns
    private static DBConnection instance;
    private Connection connection;

    private DBConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection=DriverManager.getConnection(URL, USER, PASSWORD);
      } catch (ClassNotFoundException | SQLException e){
            javax.swing.JOptionPane.showMessageDialog(null,
                "Database connection failed: " + e.getMessage(),
                "DB Error", javax.swing.JOptionPane.ERROR_MESSAGE);
      }
  }

    // Singleton getInstance
    public static DBConnection getInstance(){
        if (instance == null){
            instance=new DBConnection();
      }
        return instance;
  }

    public Connection getConnection(){
        try{
            if (connection==null||connection.isClosed()){
                connection=DriverManager.getConnection(URL, USER, PASSWORD);
          }
      } catch (SQLException e){
            JOptionPane.showMessageDialog(null,
                "Reconnection failed: " + e.getMessage(),
                "DB Error",JOptionPane.ERROR_MESSAGE);
      }
        return connection;
  }
}