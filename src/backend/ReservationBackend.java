package backend;

import db.DBConnection;
import util.ExceptionHandler;
import java.sql.*;

public class ReservationBackend{

    private final Connection conn;

    //extra charge  added to bill for reserved session
    public static final double RESERVATION_SURCHARGE=50.0;

    public ReservationBackend(){
        conn=DBConnection.getInstance().getConnection();
   }

    public boolean bookReservation(int pcId,int customerId,Date startTime,Date endTime){
        try{
    
            PreparedStatement check=conn.prepareStatement(
                    "SELECT COUNT(*) FROM reservations " +
                            "WHERE computer_id=? AND status='Pending' " +
                            "AND start_time < ? AND end_time > ?");
            check.setInt(1,pcId);
            check.setTimestamp(2,new Timestamp(endTime.getTime()));
            check.setTimestamp(3,new Timestamp(startTime.getTime()));
            ResultSet rs=check.executeQuery();
            if (rs.next() && rs.getInt(1) > 0){
                ExceptionHandler.handleValidationError(
                        "This PC is already reserved in the selected time range. Please choose a different time or PC.",null);
                return false;
           }

            PreparedStatement ps=conn.prepareStatement(
                    "INSERT INTO reservations (computer_id,customer_id,start_time,end_time,status) " +
                            "VALUES (?,?,?,?,'Pending')");
            ps.setInt(1,pcId);
            ps.setInt(2,customerId);
            ps.setTimestamp(3,new Timestamp(startTime.getTime()));
            ps.setTimestamp(4,new Timestamp(endTime.getTime()));
            ps.executeUpdate();
            return true;
       } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return false;
       }
   }

    public boolean cancelReservation(int id){
        try{
            PreparedStatement ps=conn.prepareStatement(
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
                    "SELECT r.id,c.name,COALESCE(cu.name,'?'),r.start_time,r.end_time,r.status " +
                            "FROM reservations r " +
                            "JOIN computers c ON r.computer_id=c.id " +
                            "LEFT JOIN customers cu ON r.customer_id=cu.id " +
                            "ORDER BY r.start_time DESC");
       } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return null;
       }
   }


    public boolean isPcReservedNow(int pcId){
        try{
            PreparedStatement ps=conn.prepareStatement(
                    "SELECT COUNT(*) FROM reservations " +
                            "WHERE computer_id=? AND status='Pending' " +
                            "AND start_time <= NOW() AND end_time >= NOW()");
            ps.setInt(1,pcId);
            ResultSet rs=ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
       } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return false;
       }
   }

   
   
    public boolean hasReservationSurcharge(int pcId,int customerId){
        try{
            PreparedStatement ps=conn.prepareStatement(
                    "SELECT COUNT(*) FROM reservations " +
                            "WHERE computer_id=? AND customer_id=? AND status='Pending' " +
                            "AND start_time <= NOW() AND end_time >= NOW()");
            ps.setInt(1,pcId);
            ps.setInt(2,customerId);
            ResultSet rs=ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
       } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
            return false;
       }
   }

 
 
    public void completeReservation(int pcId,int customerId){
        try{
            PreparedStatement ps=conn.prepareStatement(
                    "UPDATE reservations SET status='Completed' " +
                            "WHERE computer_id=? AND customer_id=? AND status='Pending' " +
                            "AND start_time <= NOW() AND end_time >= NOW()");
            ps.setInt(1,pcId);
            ps.setInt(2,customerId);
            ps.executeUpdate();
       } catch (SQLException e){
            ExceptionHandler.handleSQLException(e,null);
       }
   }
}