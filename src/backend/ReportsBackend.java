package backend;

import db.DBConnection;
import util.ExceptionHandler;
import java.sql.*;

public class ReportsBackend{

    private final Connection conn;

    public ReportsBackend(){
        conn = DBConnection.getInstance().getConnection();
  }

    public ResultSet getDailyRevenue(){
        try{
            return conn.createStatement().executeQuery(
                "SELECT DATE(end_time),COUNT(*),SUM(total_amount) " +
                "FROM sessions WHERE status='Ended' GROUP BY DATE(end_time) ORDER BY DATE(end_time) DESC LIMIT 30");
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return null;
      }
  }

    public ResultSet getSessionReport(){
        try{
            return conn.createStatement().executeQuery(
                "SELECT s.id,c.name,COALESCE(cu.name,'Walk-in'),s.start_time," +
                "COALESCE(CAST(s.end_time AS CHAR),'-'),s.total_amount,s.status " +
                "FROM sessions s JOIN computers c ON s.computer_id=c.id " +
                "LEFT JOIN customers cu ON s.customer_id=cu.id " +
                "ORDER BY s.start_time DESC LIMIT 100");
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return null;
      }
  }

    public ResultSet getCustomerReport(){
        try{
            return conn.createStatement().executeQuery(
                "SELECT c.id,c.name,c.phone,COALESCE(m.name,'None')," +
                "COUNT(s.id),COALESCE(SUM(s.total_amount),0) " +
                "FROM customers c LEFT JOIN memberships m ON c.membership_id=m.id " +
                "LEFT JOIN sessions s ON c.id=s.customer_id " +
                "GROUP BY c.id ORDER BY SUM(s.total_amount) DESC");
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return null;
      }
  }

    public ResultSet getPCUsageReport(){
        try{
            return conn.createStatement().executeQuery(
                "SELECT c.name,cc.name,COUNT(s.id)," +
                "COALESCE(SUM(TIMESTAMPDIFF(MINUTE,s.start_time,s.end_time))/60,0)," +
                "COALESCE(SUM(s.total_amount),0) " +
                "FROM computers c JOIN computer_categories cc ON c.category_id=cc.id " +
                "LEFT JOIN sessions s ON c.id=s.computer_id " +
                "GROUP BY c.id ORDER BY SUM(s.total_amount) DESC");
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return null;
      }
  }
}
