import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ATMSwingInterface {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("ATM");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        LoginPanel loginPanel = new LoginPanel();
        loginPanel.setOpaque(true);
        frame.setContentPane(loginPanel);

        frame.setVisible(true);
    }
}

class LoginPanel extends JPanel {
    static List<User> users;
    private User currentUser;

    private JTextField userIdField;
    private JPasswordField pinField;
    private JButton loginButton;

    public LoginPanel() {
        users = new ArrayList<>();
        // Add some sample users for testing
        users.add(new User("guna", "1234", 1000));
        users.add(new User("gunesh", "5678", 2000));
        users.add(new User("sara", "9292", 3000));

        setLayout(new GridLayout(3, 2));

        JLabel userIdLabel = new JLabel("User ID:");
        add(userIdLabel);

        userIdField = new JTextField();
        add(userIdField);

        JLabel pinLabel = new JLabel("PIN:");
        add(pinLabel);

        pinField = new JPasswordField();
        add(pinField);

        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String userId = userIdField.getText();
                String pin = new String(pinField.getPassword());

                if (authenticateUser(userId, pin)) {
                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(LoginPanel.this);
                    frame.setContentPane(new ATMMenuPanel(currentUser));
                    frame.invalidate();
                    frame.validate();
                } else {
                    JOptionPane.showMessageDialog(LoginPanel.this, "Authentication failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(loginButton);
    }

    private boolean authenticateUser(String userId, String pin) {
        for (User user : users) {
            if (user.getUserId().equals(userId) && user.getPin().equals(pin)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }
}

class ATMMenuPanel extends JPanel {
    private User currentUser;

    public ATMMenuPanel(User user) {
        currentUser = user;

        setLayout(new GridLayout(5, 1));

        JButton transactionsButton = new JButton("Transactions History");
        add(transactionsButton);

        JButton withdrawButton = new JButton("Withdraw");
        add(withdrawButton);

        JButton depositButton = new JButton("Deposit");
        add(depositButton);

        JButton transferButton = new JButton("Transfer");
        add(transferButton);

        JButton quitButton = new JButton("Quit");
        add(quitButton);

        transactionsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(ATMMenuPanel.this, "View Transactions History");
            }
        });

        withdrawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String amountString = JOptionPane.showInputDialog(ATMMenuPanel.this, "Enter the amount to withdraw:");
                double amount = Double.parseDouble(amountString);

                if (amount > 0 && amount <= currentUser.getBalance()) {
                    currentUser.setBalance(currentUser.getBalance() - amount);
                    JOptionPane.showMessageDialog(ATMMenuPanel.this, "Withdrawal successful. Remaining balance: " + currentUser.getBalance());
                } else {
                    JOptionPane.showMessageDialog(ATMMenuPanel.this, "Invalid amount or insufficient balance", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        depositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String amountString = JOptionPane.showInputDialog(ATMMenuPanel.this, "Enter the amount to deposit:");
                double amount = Double.parseDouble(amountString);

                if (amount > 0) {
                    currentUser.setBalance(currentUser.getBalance() + amount);
                    JOptionPane.showMessageDialog(ATMMenuPanel.this, "Deposit successful. New balance: " + currentUser.getBalance());
                } else {
                    JOptionPane.showMessageDialog(ATMMenuPanel.this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        transferButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String userId = JOptionPane.showInputDialog(ATMMenuPanel.this, "Enter the user ID to transfer funds:");
                User receiver = findUser(userId);

                if (receiver != null) {
                    String amountString = JOptionPane.showInputDialog(ATMMenuPanel.this, "Enter the amount to transfer:");
                    double amount = Double.parseDouble(amountString);

                    if (amount > 0 && amount <= currentUser.getBalance()) {
                        currentUser.setBalance(currentUser.getBalance() - amount);
                        receiver.setBalance(receiver.getBalance() + amount);
                        JOptionPane.showMessageDialog(ATMMenuPanel.this, "Transfer successful. Remaining balance: " + currentUser.getBalance());
                    } else {
                        JOptionPane.showMessageDialog(ATMMenuPanel.this, "Invalid amount or insufficient balance", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(ATMMenuPanel.this, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(ATMMenuPanel.this);
                frame.setContentPane(new LoginPanel());
                frame.invalidate();
                frame.validate();
            }
        });
    }

    private User findUser(String userId) {
        for (User user : LoginPanel.users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }
}

class User {
    private String userId;
    private String pin;
    private double balance;

    public User(String userId, String pin, double balance) {
        this.userId = userId;
        this.pin = pin;
        this.balance = balance;
    }

    public String getUserId() {
        return userId;
    }

    public String getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
