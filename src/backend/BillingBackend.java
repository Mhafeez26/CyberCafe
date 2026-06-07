package backend;

import db.DBConnection;
import observer.SessionEventPublisher;
import util.ExceptionHandler;
import java.sql.*;

// Backend: billing calculation, session end, receipt data
public class BillingBackend {

    private final Connection conn;
    private final ReservationBackend reservationBackend = new ReservationBackend();

    public BillingBackend() {
        conn = DBConnection.getInstance().getConnection();
    }

    public double getTaxPercent() {
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT tax_percent FROM settings LIMIT 1");
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            ExceptionHandler.handleSQLException(e, null);
        }
        return 0;
    }

    // Active sessions with billing info — includes computer_id and customer_id for surcharge check
    public ResultSet getActiveSessionsForBilling() {
        try {
            return conn.createStatement().executeQuery(
                    "SELECT s.id, c.name, COALESCE(cu.name,'Walk-in'), s.start_time, " +
                            "cc.hourly_rate, COALESCE(m.discount_percent,0), " +
                            "s.computer_id, COALESCE(s.customer_id, 0) " +
                            "FROM sessions s " +
                            "JOIN computers c ON s.computer_id = c.id " +
                            "JOIN computer_categories cc ON c.category_id = cc.id " +
                            "LEFT JOIN customers cu ON s.customer_id = cu.id " +
                            "LEFT JOIN memberships m ON cu.membership_id = m.id " +
                            "WHERE s.status = 'Active'");
        } catch (SQLException e) {
            ExceptionHandler.handleSQLException(e, null);
            return null;
        }
    }

    public boolean endSession(int sessionId, double totalAmount) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE sessions SET end_time=NOW(), total_amount=?, status='Ended' WHERE id=?");
            ps.setDouble(1, totalAmount);
            ps.setInt(2, sessionId);
            ps.executeUpdate();

            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT computer_id, COALESCE(customer_id,0) FROM sessions WHERE id=" + sessionId);
            if (rs.next()) {
                int pcId     = rs.getInt(1);
                int custId   = rs.getInt(2);

                PreparedStatement upd = conn.prepareStatement("UPDATE computers SET status='Available' WHERE id=?");
                upd.setInt(1, pcId);
                upd.executeUpdate();

                // Mark any matching reservation as Completed
                if (custId != 0) {
                    reservationBackend.completeReservation(pcId, custId);
                }

                // Design Pattern: OBSERVER — session khatam hone par notify
                SessionEventPublisher.getInstance().notifySessionEnded(pcId);
            }
            return true;
        } catch (SQLException e) {
            ExceptionHandler.handleSQLException(e, null);
            return false;
        }
    }

    public String[] getSettingsForReceipt() {
        try {
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT system_name, currency, receipt_footer FROM settings LIMIT 1");
            if (rs.next()) {
                return new String[]{rs.getString(1), rs.getString(2), rs.getString(3)};
            }
        } catch (SQLException e) {
            ExceptionHandler.handleSQLException(e, null);
        }
        return new String[]{"Cyber Cafe", "PKR", "Thank you!"};
    }

    /**
     * Calculates the bill.
     * @param reservationSurcharge  extra flat charge if session was reserved (0 if not)
     * Returns: [hours, subtotal, discAmt, taxAmt, surcharge, total]
     */
    public double[] calculateBill(double hourlyRate, long startMs,
                                  double discountPercent, double taxPercent,
                                  double reservationSurcharge) {
        double hours    = (System.currentTimeMillis() - startMs) / 3600000.0;
        double subtotal = hours * hourlyRate;
        double discAmt  = subtotal * discountPercent / 100;
        double taxAmt   = (subtotal - discAmt) * taxPercent / 100;
        double total    = subtotal - discAmt + taxAmt + reservationSurcharge;
        return new double[]{hours, subtotal, discAmt, taxAmt, reservationSurcharge, total};
    }
}