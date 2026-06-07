package backend;

import db.DBConnection;
import util.ExceptionHandler;
import java.sql.*;

public class LoginBackend {

    private final Connection conn;

    public LoginBackend() {
        conn = DBConnection.getInstance().getConnection(); // Singleton use
    }



    public String[] authenticate(String username, String password) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT u.id, u.username, r.name FROM users u JOIN roles r ON u.role_id=r.id WHERE u.username=? AND u.password=?"
            );
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{
                    String.valueOf(rs.getInt(1)),
                    rs.getString(2),
                    rs.getString(3)
                };
            }
        } catch (SQLException e) {
            ExceptionHandler.handleSQLException(e, null);
        }
        return null;
    }
}
