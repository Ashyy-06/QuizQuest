package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Admin;
import models.StudentUser;
import models.User;

public class UserDAO {
    public User login(String username, String password) {
        String sql = "SELECT id, username, password, role FROM users WHERE username = ? AND password = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String user = rs.getString("username");
                String pass = rs.getString("password");
                String role = rs.getString("role");

                if ("ADMIN".equalsIgnoreCase(role)) {
                    return new Admin(id, user, pass);
                } else {
                    return new StudentUser(id, user, pass);
                }
            }
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        return null;
    }

    public boolean register(String username, String password) {
        return createUser(username, password, "USER");
    }

    public boolean createUser(String username, String password, String role) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role.toUpperCase());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("User creation failed: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT id, username, password, role FROM users ORDER BY id ASC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");

                if ("ADMIN".equalsIgnoreCase(role)) {
                    userList.add(new Admin(id, username, password));
                } else {
                    userList.add(new StudentUser(id, username, password));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        return userList;
    }

    public boolean updateUser(int userId, String newPassword, String newRole) {
        String sql = "UPDATE users SET password = ?, role = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newPassword);
            stmt.setString(2, newRole.toUpperCase());
            stmt.setInt(3, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println("Failed to close ResultSet: " + e.getMessage());
        }

        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            System.err.println("Failed to close Statement: " + e.getMessage());
        }

        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Failed to close Connection: " + e.getMessage());
        }
    }
}
