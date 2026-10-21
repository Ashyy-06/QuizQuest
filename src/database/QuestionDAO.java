package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import models.Question;
import models.SingleChoiceMCQ;
import models.TrueFalseQuestion;

public class QuestionDAO {

    public void seedQuestionsIfEmpty() {
        String countSql = "SELECT COUNT(*) FROM questions";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        boolean isEmpty = false;

        try {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(countSql);
            if (rs.next() && rs.getInt(1) == 0) {
                isEmpty = true;
            }
        } catch (SQLException e) {
            System.err.println("Error checking question count: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }

        if (isEmpty) {
            insertDefaultQuestions();
        }
    }

    private void insertDefaultQuestions() {
        
        addQuestion("Science", "MCQ", "The chemical reaction between copper and oxygen can be categorized as:", 
                "Displacement reaction|Decomposition reaction|Combination reaction|Double displacement reaction", "3");
        addQuestion("Science", "MCQ", "Opening and closing of stomatal pore depends on:", 
                "Atmospheric temperature|Oxygen concentration around stomata|Carbon dioxide concentration around stomata|Water content in the guard cells", "4");
        addQuestion("Science", "MCQ", "Which of the following mirror is used by a dentist to examine a small cavity in a patient's teeth?", 
                "Convex mirror|Plane mirror|Concave mirror|Any spherical mirror", "3");
        addQuestion("Science", "MCQ", "Which of the following can make a parallel beam of light when light from a point source is incident on it?", 
                "Concave mirror as well as convex lens|Convex mirror as well as concave lens|Two plane mirrors placed at 90 degrees|Concave mirror as well as concave lens", "1");
        addQuestion("Science", "MCQ", "In which group of organisms does blood flow through the heart only once during one cycle?", 
                "Rabbit, Parrot, Turtle|Frog, crocodile, Pigeon|Whale, Labeo, Penguin|Shark, dog fish, sting ray", "4");
        addQuestion("Science", "MCQ", "What is common between blood vessels around alveoli walls and glomerulus of nephron?", 
                "Thick walled arteries poorly supplied|Thin walled veins poorly supplied|Thick walled capillaries poorly supplied|Thin walled capillaries richly supplied with blood", "4");
        addQuestion("Science", "MCQ", "Which one of the following processes is NOT followed by plants for excretion?", 
                "Get rid of excess water by transpiration|Selectively filter toxic substances through leaves|Waste products stored as resins and gums|Excrete waste substances into soil", "2");
        addQuestion("Science", "MCQ", "In which organisms is food material broken down outside the body and then absorbed?", 
                "Mushroom, green plants, amoeba|Yeast, mushroom, bread mould|Paramecium, amoeba, cuscuta|Cuscuta, lice, tapeworm", "2");
        addQuestion("Science", "MCQ", "If the tubule part of the nephron is not functioning, what is its effect on urine formation?", 
                "Urine will not be formed|Quality and quantity unaffected|Urine is more concentrated|Urine is more diluted", "4");
        addQuestion("Science", "MCQ", "What is the saturated solution of Sodium Chloride called?", 
                "Brine|Lime water|Slaked lime|Soda water", "1");
        addQuestion("Science", "TRUE_FALSE", "Concave mirrors always form virtual and erect images regardless of object position.", "", "false");
        addQuestion("Science", "TRUE_FALSE", "Transpiration in plants aids in temperature regulation and water transport.", "", "true");

        addQuestion("Social Science", "MCQ", "Who among the following was the founder of 'Young Europe' secret society in Berne?", 
                "Giuseppe Garibaldi|Giuseppe Mazzini|Count Cavour|Otto von Bismarck", "2");
        addQuestion("Social Science", "MCQ", "Which of the following was the result of the Act of Union, 1707?", 
                "Unification of Germany|Unification of Great Britain|Unification of Italy|Unification of Vietnam", "2");
        addQuestion("Social Science", "MCQ", "Identify the correct statement with regard to the 'Zollverein'.", 
                "Coalition to manage political alliances|Formed to bind Prussia politically|Customs Union at the initiative of Prussia|Awakened national sentiment in Europe", "3");
        addQuestion("Social Science", "MCQ", "Which countries were involved in the Three Wars with Prussia?", 
                "Austria, Poland and France|Austria, Denmark and France|Austria, Turkey and France|Austria, England and France", "2");
        addQuestion("Social Science", "MCQ", "Identify the ideology under which people demanded freedom of markets in 19th-century Europe.", 
                "Romanticism|Liberalism|Socialism|Rationalism", "2");
        addQuestion("Social Science", "MCQ", "Choose the correctly matched pair about crops and where they are grown.", 
                "Groundnut - Assam|Tea - Gujarat|Coffee - Karnataka|Sugarcane - Chhattisgarh", "3");
        addQuestion("Social Science", "MCQ", "The piece of land left uncultivated for the past 1 to 5 agricultural years is called:", 
                "Barren land|Forest land|Grazing land|Fallow land", "4");
        addQuestion("Social Science", "MCQ", "Which conference was convened in 1992 to discuss environmental protection globally?", 
                "Kyoto Protocol|Montreal Protocol|Rio de Janeiro Earth Summit|World Summit on Sustainable Development", "3");
        addQuestion("Social Science", "MCQ", "Which human activity has contributed significantly to land degradation?", 
                "Deforestation|Crop rotation|Shelter belts|Ploughing", "1");
        addQuestion("Social Science", "MCQ", "Which country is an example of a 'Coming Together Federation'?", 
                "India|Sri Lanka|USA|Belgium", "3");
        addQuestion("Social Science", "TRUE_FALSE", "Sri Lanka adopted majoritarianism in its Constitution.", "", "true");
        addQuestion("Social Science", "TRUE_FALSE", "The courier service belongs to the secondary sector of the economy.", "", "false");
    }

    public boolean addQuestion(String topic, String type, String prompt, String options, String correctAnswer) {
        String sql = "INSERT INTO questions (topic, type, question_text, options, correct_answer) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, topic);
            stmt.setString(2, type.toUpperCase());
            stmt.setString(3, prompt);
            stmt.setString(4, options);
            stmt.setString(5, correctAnswer);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding question: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    public List<Question> getRandomQuestionsByTopic(String topic, int limit) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT id, topic, type, question_text, options, correct_answer "
                   + "FROM questions WHERE topic = ? ORDER BY RANDOM() LIMIT ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, topic);
            stmt.setInt(2, limit);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String id = String.valueOf(rs.getInt("id"));
                String type = rs.getString("type");
                String prompt = rs.getString("question_text");
                String optionsStr = rs.getString("options");
                String answer = rs.getString("correct_answer");

                if ("MCQ".equalsIgnoreCase(type)) {
                    List<String> options = Arrays.asList(optionsStr.split("\\|"));
                    int correctIdx = Integer.parseInt(answer.trim());
                    questions.add(new SingleChoiceMCQ(id, prompt, 1, options, correctIdx));
                } else if ("TRUE_FALSE".equalsIgnoreCase(type)) {
                    boolean correctBool = Boolean.parseBoolean(answer.trim());
                    questions.add(new TrueFalseQuestion(id, prompt, 1, correctBool));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching random questions: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        return questions;
    }

    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT id, topic, type, question_text, options, correct_answer FROM questions ORDER BY id ASC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String id = String.valueOf(rs.getInt("id"));
                String type = rs.getString("type");
                String prompt = rs.getString("question_text");
                String optionsStr = rs.getString("options");
                String answer = rs.getString("correct_answer");

                if ("MCQ".equalsIgnoreCase(type)) {
                    List<String> options = Arrays.asList(optionsStr.split("\\|"));
                    int correctIdx = Integer.parseInt(answer.trim());
                    questions.add(new SingleChoiceMCQ(id, prompt, 1, options, correctIdx));
                } else if ("TRUE_FALSE".equalsIgnoreCase(type)) {
                    boolean correctBool = Boolean.parseBoolean(answer.trim());
                    questions.add(new TrueFalseQuestion(id, prompt, 1, correctBool));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all questions: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        return questions;
    }

    public boolean deleteQuestion(int id) {
        String sql = "DELETE FROM questions WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting question: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    public boolean updateQuestion(int id, String newPrompt, String newOptions, String newAnswer) {
        String sql = "UPDATE questions SET question_text = ?, options = ?, correct_answer = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newPrompt);
            stmt.setString(2, newOptions);
            stmt.setString(3, newAnswer);
            stmt.setInt(4, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating question: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            System.err.println("Failed to close ResultSet: " + e.getMessage());
        }

        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            System.err.println("Failed to close Statement: " + e.getMessage());
        }

        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Failed to close Connection: " + e.getMessage());
        }
    }
}