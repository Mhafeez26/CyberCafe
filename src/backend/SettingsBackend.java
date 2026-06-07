package backend;

import db.DBConnection;
import util.ExceptionHandler;
import java.sql.*;

public class SettingsBackend{

    private final Connection conn;

    public SettingsBackend(){
        conn = DBConnection.getInstance().getConnection();
    }

    public String[] loadSettings(){
        try{
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT system_name,tax_percent,currency,receipt_footer FROM settings LIMIT 1");
            if (rs.next()){
                return new String[]{
                    rs.getString(1),
                    String.valueOf(rs.getDouble(2)),
                    rs.getString(3),
                    rs.getString(4)
                };
            }
        } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
        }
        return new String[]{"Cyber Cafe","0","PKR","Thank you!"};
    }

    public boolean saveSettings(String systemName,double tax,String currency,String footer){
        try{
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE settings SET system_name=?,tax_percent=?,currency=?,receipt_footer=?");
            ps.setString(1,systemName); ps.setDouble(2,tax);
            ps.setString(3,currency); ps.setString(4,footer);
            ps.executeUpdate();
            return true;
        } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return false;
        }
    }
}
