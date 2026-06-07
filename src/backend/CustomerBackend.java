package backend;

import db.DBConnection;
import util.ExceptionHandler;
import java.sql.*;


public class CustomerBackend{

    private final Connection conn;

    public CustomerBackend(){
        conn=DBConnection.getInstance().getConnection();
  }

    public boolean addCustomer(String name,String phone,String email,int membershipId){
        try{
            PreparedStatement ps=conn.prepareStatement(


                "INSERT INTO customers (name,phone,email,membership_id) VALUES (?,?,?,?)");
            ps.setString(1,name); ps.setString(2,phone); ps.setString(3,email); ps.setInt(4,membershipId);
            ps.executeUpdate();
            return true;
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return false;
      }
  }

    public boolean updateCustomer(int id,String name,String phone,String email,int membershipId){
        try{
            PreparedStatement ps=conn.prepareStatement(
                "UPDATE customers SET name=?,phone=?,email=?,membership_id=? WHERE id=?");
            ps.setString(1,name); ps.setString(2,phone); ps.setString(3,email);
            ps.setInt(4,membershipId); ps.setInt(5,id);
            ps.executeUpdate();
            return true;
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return false;
      }
  }

    public boolean deleteCustomer(int id){


        try{
            PreparedStatement ps=conn.prepareStatement("DELETE FROM customers WHERE id=?");
            ps.setInt(1,id);
            ps.executeUpdate();
            return true;
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return false;
      }
  }

    public ResultSet searchCustomers(String keyword){
        try{
            String sql="SELECT c.id,c.name,c.phone,c.email,COALESCE(m.name,'None') " +
                "FROM customers c LEFT JOIN memberships m ON c.membership_id=m.id";
            if (keyword != null && !keyword.isEmpty())
                sql += " WHERE c.name LIKE ? OR c.phone LIKE ?";
            PreparedStatement ps=conn.prepareStatement(sql);

            if (keyword != null && !keyword.isEmpty()){

                ps.setString(1,"%" + keyword + "%");
                ps.setString(2,"%" + keyword + "%");
          }
            return ps.executeQuery();
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return null;
      }

  }

    public ResultSet getAllMemberships(){
        try{
            return conn.createStatement().executeQuery(
                "SELECT id,name,discount_percent FROM memberships");
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return null;
      }
  }

    public ResultSet getAllCustomersForCombo(){
        try{
            return conn.createStatement().executeQuery("SELECT id,name FROM customers");
      } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return null;
      }
  }
}
