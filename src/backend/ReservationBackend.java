package backend;

import db.DBConnection;
import util.ExceptionHandler;
import java.sql.*;

public class ReservationBackend{

    private final Connection conn;

    public ReservationBackend(){
        conn = DBConnection.getInstance().getConnection(); 
    }

    public boolean bookReservation(int pcId,int customerId,java.util.Date date){
        try{
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO reservations (computer_id,customer_id,reservation_date,status) VALUES (?,?,?,'Pending')");
            ps.setInt(1,pcId); ps.setInt(2,customerId);
            ps.setTimestamp(3,new Timestamp(date.getTime()));
            ps.executeUpdate();
            return true;
        } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return false;
        }
    }

    public boolean cancelReservation(int id){
        try{
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE reservations SET status='Cancelled' WHERE id=?");
            ps.setInt(1,id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return false;
        }
    }

    public ResultSet getAllReservations(){
        try{
            return conn.createStatement().executeQuery(
                "SELECT r.id,c.name,COALESCE(cu.name,'?'),r.reservation_date,r.status " +
                "FROM reservations r JOIN computers c ON r.computer_id=c.id " +
                "LEFT JOIN customers cu ON r.customer_id=cu.id ORDER BY r.reservation_date DESC");
        } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return null;
        }
    }
}
