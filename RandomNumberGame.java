import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RandomNumberGame extends JFrame {

    private int randomNumber;
    private static int score=100;
    private int attempts;
    private JTextField guessField;
    private JTextArea resultArea;
    private JLabel scoreLabel;

    public RandomNumberGame() {
        setTitle("Random Number Guessing Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JLabel label = new JLabel("Enter your guess:");
        label.setBackground(Color.GREEN);
        guessField = new JTextField(20);
        JButton guessButton = new JButton("Guess");
        guessButton.addActionListener(new GuessButtonListener());
        guessButton.setBackground(Color.pink);
        guessButton.setBorderPainted(true);
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);

        scoreLabel = new JLabel("Score: 0");

        add(label);
        add(guessField);
        add(guessButton);
        add(resultArea);
        add(scoreLabel);
        
        generateRandomNumber();

        pack();
        setVisible(true);
    }

    private void generateRandomNumber() {
        randomNumber = (int) (Math.random() * 100) + 1;
        attempts = 0;
    }

    private class GuessButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int guess = Integer.parseInt(guessField.getText());

                attempts++;
                score/=attempts;

                if (guess == randomNumber) {
                    resultArea.append("Congratulations! You guessed the correct number: " + guess + "\n"+"SCORE:"+score);
                    resultArea.append("Play again!\n");
                    generateRandomNumber();
                    score = 0;
                    attempts = 0;
                } 
                else if (guess+1==randomNumber || guess-1==randomNumber|| guess-2==randomNumber|| guess+2==randomNumber)
                {
                    resultArea.append("Heyy!!...You're too close!!\n");
                }else if (guess < randomNumber) {
                    resultArea.append("Argghh!! You Guessed too low! Try again.\n");
                } else {
                    resultArea.append("Argghh!! You Guessed too high! Try again.\n");
                }

                scoreLabel.setText("Score: " + score);
                guessField.setText("");

                if (attempts == 5) {
                    resultArea.append("Out of attempts! The number was: " + randomNumber + "\n");
                    resultArea.append("Play again!\n");
                    generateRandomNumber();
                    score = 0;
                    attempts = 0;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RandomNumberGame();
            }
        });
    }
}
