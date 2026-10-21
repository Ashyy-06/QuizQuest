package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.QuizResult;

public class QuizResultDAO 
{

    public boolean saveResult(int userId, String topic, int totalQuestions, int correctAnswers) {
        String sql = "INSERT INTO quiz_results (user_id, topic, score, total) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try 
        {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, topic);
            stmt.setInt(3, correctAnswers);
            stmt.setInt(4, totalQuestions);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) 
        {
            System.err.println("Error saving quiz result: " + e.getMessage());
            return false;
        } finally 
        {
            closeResources(null, stmt, conn);
        }
    }

    public List<QuizResult> getResultsByUserId(int userId) 
    {
        List<QuizResult> list = new ArrayList<>();
        String sql = "SELECT qr.id, qr.user_id, u.username, qr.topic, qr.score, qr.total "
                   + "FROM quiz_results qr "
                   + "JOIN users u ON qr.user_id = u.id "
                   + "WHERE qr.user_id = ? "
                   + "ORDER BY qr.id DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try 
        {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            while (rs.next()) 
            {
                int resultId = rs.getInt("id");
                int uId = rs.getInt("user_id");
                String username = rs.getString("username");
                String topic = rs.getString("topic");
                int score = rs.getInt("score");
                int total = rs.getInt("total");

                list.add(new QuizResult(resultId, uId, username, topic, total, score));
            }
        } catch (SQLException e) 
        {
            System.err.println("Error fetching user quiz history: " + e.getMessage());
        } finally 
        {
            closeResources(rs, stmt, conn);
        }
        return list;
    }

    public List<QuizResult> getAllResults() 
    {
        List<QuizResult> list = new ArrayList<>();
        String sql = "SELECT qr.id, qr.user_id, u.username, qr.topic, qr.score, qr.total "
                   + "FROM quiz_results qr "
                   + "JOIN users u ON qr.user_id = u.id "
                   + "ORDER BY qr.id DESC";

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
                int resultId = rs.getInt("id");
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String topic = rs.getString("topic");
                int score = rs.getInt("score");
                int total = rs.getInt("total");

                list.add(new QuizResult(resultId, userId, username, topic, total, score));
            }
        } catch (SQLException e) 
        {
            System.err.println("Error fetching all quiz results: " + e.getMessage());
        } finally 
        {
            closeResources(rs, stmt, conn);
        }
        return list;
    }

    public boolean deleteResult(int resultId) 
    {
        String sql = "DELETE FROM quiz_results WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try 
        {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, resultId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) 
        {
            System.err.println("Error deleting quiz result: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    public boolean updateResult(int resultId, int newScore, int newTotal) 
    {
        String sql = "UPDATE quiz_results SET score = ?, total = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try 
        {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, newScore);
            stmt.setInt(2, newTotal);
            stmt.setInt(3, resultId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) 
        {
            System.err.println("Error updating quiz result: " + e.getMessage());
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
            if (rs != null) 
            {
                rs.close();
            }
        } catch (SQLException e) 
        {
            System.err.println("Failed to close ResultSet: " + e.getMessage());
        }

        try 
        {
            if (stmt != null) 
            {
                stmt.close();
            }
        } catch (SQLException e) 
        {
            System.err.println("Failed to close Statement: " + e.getMessage());
        }

        try 
        {
            if (conn != null) 
            {
                conn.close();
            }
        } catch (SQLException e) 
        {
            System.err.println("Failed to close Connection: " + e.getMessage());
        }
    }
}