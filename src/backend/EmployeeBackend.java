package backend;

import db.DBConnection;
import util.ExceptionHandler;
import java.sql.*;

public class EmployeeBackend{

    private final Connection conn;
    public EmployeeBackend(){
        conn=DBConnection.getInstance().getConnection();
   }

    public boolean addEmployee(String name,String phone,String position,double salary,
                               String username,String password,int roleId){
        try{
            int userId=-1;
            
            if (!username.isEmpty() && !password.isEmpty()){
            
                PreparedStatement ps2=conn.prepareStatement(
            
                    "INSERT INTO users (username,password,role_id) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
                ps2.setString(1,username); ps2.setString(2,password); ps2.setInt(3,roleId);
                ps2.executeUpdate();
                ResultSet gk=ps2.getGeneratedKeys();
            
                if (gk.next()) userId=gk.getInt(1);
           }

            PreparedStatement ps=conn.prepareStatement(
            
                "INSERT INTO employees (name,phone,position,salary,user_id) VALUES (?,?,?,?,?)");
            
            ps.setString(1,name); ps.setString(2,phone);
            ps.setString(3,position); ps.setDouble(4,salary);
            
            if (userId < 0) ps.setNull(5,Types.INTEGER); else ps.setInt(5,userId);
            ps.executeUpdate();
            
            return true;
       } 
        catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return false;
       }
   }
    

    public boolean updateEmployee(int id,String name,String phone,String position,double salary){
    
        try{
            PreparedStatement ps=conn.prepareStatement(
    
                "UPDATE employees SET name=?,phone=?,position=?,salary=? WHERE id=?");
            ps.setString(1,name); ps.setString(2,phone);
            ps.setString(3,position); ps.setDouble(4,salary); ps.setInt(5,id);
            ps.executeUpdate();
            return true;
       } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return false;
       }
   }

    public boolean deleteEmployee(int id){
        try{
            PreparedStatement ps=conn.prepareStatement("DELETE FROM employees WHERE id=?");
            ps.setInt(1,id);
            ps.executeUpdate();
            return true;
       } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return false;
       }
   }

    public ResultSet searchEmployees(String keyword){
        try{
            String sql="SELECT id,name,phone,position,salary FROM employees";
            if (keyword != null && !keyword.isEmpty())
                sql += " WHERE name LIKE ? OR phone LIKE ?";
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

    public ResultSet getAllRoles(){
        try{
            return conn.createStatement().executeQuery("SELECT id,name FROM roles");
       } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return null;
       }
   }
}
