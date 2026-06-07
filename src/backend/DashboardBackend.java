package backend;

import db.DBConnection;
import util.ExceptionHandler;
import java.sql.*;

public class DashboardBackend {

    private final Connection conn;

    public DashboardBackend() {
        conn = DBConnection.getInstance().getConnection(); //singlton use
    }

    public int getTotalComputers() {
        return getCount("SELECT COUNT(*) FROM computers");
    }

    public int getActiveSessions() {
        return getCount("SELECT COUNT(*) FROM sessions WHERE status='Active'");
    }

    public int getAvailableComputers() {
        return getCount("SELECT COUNT(*) FROM computers WHERE status='Available'");
    }

    public double getTodayRevenue() {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT COALESCE(SUM(total_amount),0) FROM sessions WHERE DATE(end_time)=CURDATE()"
            );
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            ExceptionHandler.handleSQLException(e, null);
        }
        return 0.0;
    }



    public ResultSet getLivePCStatus() {
        try {
            return conn.createStatement().executeQuery(
                "SELECT c.name, c.status, cc.name FROM computers c JOIN computer_categories cc ON c.category_id=cc.id"
            );
        } catch (SQLException e) {
            ExceptionHandler.handleSQLException(e, null);
            return null;
        }
    }

    private int getCount(String sql) {
        try {
            ResultSet rs = conn.createStatement().executeQuery(sql);

            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            ExceptionHandler.handleSQLException(e, null);
        }
        return 0;
    }

}
