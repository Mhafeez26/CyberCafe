package backend;

import db.DBConnection;
import observer.SessionEventPublisher;
import util.ExceptionHandler;
import java.sql.*;

public class SessionBackend {

    private final Connection conn;
    private final ReservationBackend reservationBackend = new ReservationBackend();

    public SessionBackend() {
        conn = DBConnection.getInstance().getConnection();
    }

    /**
     * Starts a session. Blocks if the PC is currently reserved by another customer.
     * If this customer has a matching reservation, the session is allowed and the
     * reservation is marked as 'Completed' upon session end.
     */
    public boolean startSession(int pcId, int customerId) {
        try {
            // Block if PC is reserved right now and this is a walk-in (customerId == 0)
            if (customerId == 0 && reservationBackend.isPcReservedNow(pcId)) {
                ExceptionHandler.handleValidationError(
                        "This PC is currently reserved. It cannot be used for a walk-in session.", null);
                return false;
            }

            // Block if PC is reserved for a different customer right now
            if (customerId != 0 && reservationBackend.isPcReservedNow(pcId)
                    && !reservationBackend.hasReservationSurcharge(pcId, customerId)) {
                ExceptionHandler.handleValidationError(
                        "This PC is reserved for another customer during this time slot.", null);
                return false;
            }

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO sessions (computer_id, customer_id, start_time, status) VALUES (?, ?, NOW(), 'Active')");
            ps.setInt(1, pcId);
            if (customerId == 0) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, customerId);
            ps.executeUpdate();

            PreparedStatement upd = conn.prepareStatement("UPDATE computers SET status='Occupied' WHERE id=?");
            upd.setInt(1, pcId);
            upd.executeUpdate();

            // Design Pattern: OBSERVER — session start hone par saare observers ko notify karo
            SessionEventPublisher.getInstance().notifySessionStarted(pcId);
            return true;
        } catch (SQLException e) {
            ExceptionHandler.handleSQLException(e, null);
            return false;
        }
    }

    public ResultSet getActiveSessions() {
        try {
            return conn.createStatement().executeQuery(
                    "SELECT s.id, c.name, COALESCE(cu.name,'Walk-in'), s.start_time, s.status " +
                            "FROM sessions s JOIN computers c ON s.computer_id = c.id " +
                            "LEFT JOIN customers cu ON s.customer_id = cu.id " +
                            "WHERE s.status = 'Active' ORDER BY s.start_time DESC");
        } catch (SQLException e) {
            ExceptionHandler.handleSQLException(e, null);
            return null;
        }
    }

    public String formatDuration(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}