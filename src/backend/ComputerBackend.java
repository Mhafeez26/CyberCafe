package backend;

import db.DBConnection;
import util.ExceptionHandler;
import java.sql.*;



public class ComputerBackend{

    private final Connection conn;

    public ComputerBackend(){
        conn=DBConnection.getInstance().getConnection();
  }

    public boolean addComputer(String name,int categoryId,String status){
        try{
            PreparedStatement ps=conn.prepareStatement(
                "INSERT INTO computers (name,category_id,status) VALUES (?,?,?)");
            ps.setString(1,name); ps.setInt(2,categoryId); ps.setString(3,status);
            ps.executeUpdate();
            return true;
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return false;
      }
  }

    public boolean updateComputer(int id,String name,int categoryId,String status){
        try{
            PreparedStatement ps=conn.prepareStatement(
                "UPDATE computers SET name=?,category_id=?,status=? WHERE id=?");
            ps.setString(1,name); ps.setInt(2,categoryId); ps.setString(3,status); ps.setInt(4,id);
            ps.executeUpdate();
            return true;
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return false;
      }
  }
    
    

    public boolean deleteComputer(int id){
        try{
            PreparedStatement ps=conn.prepareStatement("DELETE FROM computers WHERE id=?");
    
            ps.setInt(1,id);
            ps.executeUpdate();
            return true;
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return false;
      }
  }

    public ResultSet getAllComputers(){
        try{
            return conn.createStatement().executeQuery(
                "SELECT c.id,c.name,cc.name,cc.hourly_rate,c.status,cc.id FROM computers c JOIN computer_categories cc ON c.category_id=cc.id");
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return null;
      }
  }

    public ResultSet getAllCategories(){
        try{
            return conn.createStatement().executeQuery(
                "SELECT id,name,hourly_rate FROM computer_categories");
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return null;
      }
  }

    public ResultSet getAllComputersForCombo(){
        try{
            return conn.createStatement().executeQuery("SELECT id,name FROM computers");
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return null;
      }
  }

    public ResultSet getAvailableComputers(){
        try{
            return conn.createStatement().executeQuery(
                "SELECT id,name FROM computers WHERE status='Available'");
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return null;
      }
  }

    public boolean updateStatus(int id,String status){
        try{
            PreparedStatement ps=conn.prepareStatement("UPDATE computers SET status=? WHERE id=?");
            ps.setString(1,status); ps.setInt(2,id);
            ps.executeUpdate();
            return true;
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return false;
      }
  }

    public boolean addGame(String name,int computerId){
        try{
            PreparedStatement ps=conn.prepareStatement(
                "INSERT INTO games (name,computer_id) VALUES (?,?)");
            ps.setString(1,name); ps.setInt(2,computerId);
            ps.executeUpdate();
            return true;
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return false;
      }
  }

    public boolean deleteGame(int id){
        try{
            PreparedStatement ps=conn.prepareStatement("DELETE FROM games WHERE id=?");
            ps.setInt(1,id);
            ps.executeUpdate();
            return true;
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return false;
      }
  }

    public ResultSet getAllGames(){
        try{
            return conn.createStatement().executeQuery(
                "SELECT g.id,g.name,c.name FROM games g JOIN computers c ON g.computer_id=c.id");
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return null;
      }
  }
}
