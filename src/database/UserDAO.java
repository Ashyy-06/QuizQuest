package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.Admin;
import models.StudentUser;
import models.User;

public class UserDAO 
{

    public void seedAdminIfEmpty() 
    {
        String checkSql = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";
        String insertSql = "INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'ADMIN')";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        boolean needsAdmin = false;

        try 
        {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(checkSql);
            if (rs.next() && rs.getInt(1) == 0) 
            {
                needsAdmin = true;
            }
        } catch (SQLException e) 
        {
            System.err.println("Error checking admin existence: " + e.getMessage());
        } finally 
        {
            closeResources(rs, stmt, conn);
        }

        if (needsAdmin) 
        {
            Connection insertConn = null;
            Statement insertStmt = null;
            try 
            {
                insertConn = DatabaseManager.getInstance().getConnection();
                insertStmt = insertConn.createStatement();
                insertStmt.executeUpdate(insertSql);
                System.out.println("Default admin created: admin / admin123");
            } catch (SQLException e) 
            {
                System.err.println("Error seeding default admin: " + e.getMessage());
            } finally 
            {
                closeResources(null, insertStmt, insertConn);
            }
        }
    }

    public boolean register(String username, String password) 
    {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'STUDENT')";
        Connection conn = null;
        PreparedStatement stmt = null;

        try 
        {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) 
        {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        } finally 
        {
            closeResources(null, stmt, conn);
        }
    }

    public User login(String username, String password) 
    {
        String sql = "SELECT id, username, password, role FROM users WHERE username = ? AND password = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try 
        {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            rs = stmt.executeQuery();
            if (rs.next()) 
            {
                int id = rs.getInt("id");
                String user = rs.getString("username");
                String pass = rs.getString("password");
                String role = rs.getString("role");

                if ("ADMIN".equalsIgnoreCase(role)) 
                {
                    return new Admin(id, user, pass);
                } else 
                {
                    return new StudentUser(id, user, pass);
                }
            }
        } catch (SQLException e) 
        {
            System.err.println("Error during login: " + e.getMessage());
        } finally 
        {
            closeResources(rs, stmt, conn);
        }
        return null;
    }

    public List<User> getAllUsers() 
    {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password, role FROM users ORDER BY id ASC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try 
        {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) 
            {
                int id = rs.getInt("id");
                String user = rs.getString("username");
                String pass = rs.getString("password");
                String role = rs.getString("role");

                if ("ADMIN".equalsIgnoreCase(role)) 
                {
                    users.add(new Admin(id, user, pass));
                } else 
                {
                    users.add(new StudentUser(id, user, pass));
                }
            }
        } catch (SQLException e) 
        {
            System.err.println("Error fetching all users: " + e.getMessage());
        } finally 
        {
            closeResources(rs, stmt, conn);
        }
        return users;
    }

    public boolean updateUser(int userId, String newPassword, String newRole) 
    {
        String sql = "UPDATE users SET password = ?, role = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try 
        {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newPassword);
            stmt.setString(2, newRole.toUpperCase());
            stmt.setInt(3, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) 
        {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        } finally 
        {
            closeResources(null, stmt, conn);
        }
    }

    public boolean deleteUser(int userId) 
    {
        String sql = "DELETE FROM users WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try 
        {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) 
        {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        } finally 
        {
            closeResources(null, stmt, conn);
        }
    }

    private void closeResources(ResultSet rs, Statement stmt, Connection conn) 
    {

        try 
        {
            if (rs != null) rs.close();
        } catch (SQLException e) 
        {
            System.err.println("Failed to close ResultSet: " + e.getMessage());
        }

        try 
        {
            if (stmt != null) stmt.close();
        } catch (SQLException e) 
        {
            System.err.println("Failed to close Statement: " + e.getMessage());
        }

        try 
        {
            if (conn != null) conn.close();
        } catch (SQLException e) 
        {
            System.err.println("Failed to close Connection: " + e.getMessage());
        }
    }
}