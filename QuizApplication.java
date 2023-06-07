import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class QuizApplication extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private int loggedInUserId; // Track the logged-in user ID

    public QuizApplication() {
        setTitle("Quiz Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                boolean authenticated = authenticate(username, password);

                if (authenticated) {
                    dispose();
                    startQuiz();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password. Please try again.");
                }
            }
        });

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);

        setVisible(true);
    }

    private boolean authenticate(String username, String password) {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                loggedInUserId = resultSet.getInt("id"); // Store the logged-in user ID
                return dbPassword.equals(password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void startQuiz() {
        int timeLimitInSeconds = 60;
        int numQuestions = 5;

        JOptionPane.showMessageDialog(null, "Welcome to the Quiz!");

        Timer timer = new Timer(timeLimitInSeconds * 1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitQuiz();
            }
        });
        timer.setRepeats(false);
        timer.start();

        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM questions ORDER BY RAND() LIMIT " + numQuestions);

            while (resultSet.next()) {
                int questionId = resultSet.getInt("id");
                String questionText = resultSet.getString("question");
                String optionA = resultSet.getString("option_a");
                String optionB = resultSet.getString("option_b");
                String optionC = resultSet.getString("option_c");
                String optionD = resultSet.getString("option_d");

                JPanel questionPanel = new JPanel();
                questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));

                JLabel questionLabel = new JLabel(questionText);
                questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                questionPanel.add(questionLabel);

                ButtonGroup optionGroup = new ButtonGroup();

                JRadioButton optionARadioButton = new JRadioButton(optionA);
                optionARadioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                optionGroup.add(optionARadioButton);
                questionPanel.add(optionARadioButton);

                JRadioButton optionBRadioButton = new JRadioButton(optionB);
                optionBRadioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                optionGroup.add(optionBRadioButton);
                questionPanel.add(optionBRadioButton);

                JRadioButton optionCRadioButton = new JRadioButton(optionC);
                optionCRadioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                optionGroup.add(optionCRadioButton);
                questionPanel.add(optionCRadioButton);

                JRadioButton optionDRadioButton = new JRadioButton(optionD);
                optionDRadioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                optionGroup.add(optionDRadioButton);
                questionPanel.add(optionDRadioButton);

                int optionChosen = JOptionPane.showOptionDialog(null, questionPanel, "Question", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
                String answer = getOptionLetter(optionChosen);

                saveAnswer(connection, questionId, answer);
            }

            submitQuiz();

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getOptionLetter(int optionIndex) {
        switch (optionIndex) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            default:
                return "";
        }
    }

    private void saveAnswer(Connection connection, int questionId, String answer) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO user_answers (user_id, question_id, answer) VALUES (?, ?, ?)");
            statement.setInt(1, loggedInUserId);
            statement.setInt(2, questionId);
            statement.setString(3, answer);
            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void submitQuiz() {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS total FROM questions");
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int totalQuestions = resultSet.getInt("total");

            int score = calculateScore(connection, loggedInUserId, totalQuestions);
            JOptionPane.showMessageDialog(null, "Quiz submitted!\nYour score: " + score);

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    private int calculateScore(Connection connection, int userId, int totalQuestions) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS correct FROM user_answers ua JOIN questions q ON ua.question_id = q.id WHERE ua.user_id = ? AND ua.answer = q.correct_answer");
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int correctAnswers = resultSet.getInt("correct");

            return (correctAnswers * 100) / totalQuestions;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/quiz_db";
        String username = "root";
        String password = "guna9292";
        return DriverManager.getConnection(url, username, password);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new QuizApplication();
            }
        });
    }
}
