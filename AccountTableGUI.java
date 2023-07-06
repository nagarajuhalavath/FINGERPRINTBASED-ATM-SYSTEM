package fingerprint;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountTableGUI extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtAccountNo, txtId, txtAccountType, txtBalance;
    private JTable tblAccounts;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public AccountTableGUI() {
        initializeUI();
        connectToDatabase();
        displayAccounts();
    }

    private void initializeUI() {
        txtAccountNo = new JTextField();
        txtId = new JTextField();
        txtAccountType = new JTextField();
        txtBalance = new JTextField();

        tblAccounts = new JTable();
        tblAccounts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAccounts.getSelectionModel().addListSelectionListener(e -> selectAccount());

        JScrollPane scrollPane = new JScrollPane(tblAccounts);

        btnAdd = new JButton("Add");
        btnModify = new JButton("Modify");
        btnDelete = new JButton("Delete");
        btnDisplay = new JButton("Display");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("Account No:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Account Type:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Balance:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtAccountNo, gbc);
        gbc.gridy++;
        panel.add(txtId, gbc);
        gbc.gridy++;
        panel.add(txtAccountType, gbc);
        gbc.gridy++;
        panel.add(txtBalance, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;

        panel.add(btnAdd, gbc);
        gbc.gridy++;
        panel.add(btnModify, gbc);
        gbc.gridy++;
        panel.add(btnDelete, gbc);
        gbc.gridy++;
        panel.add(btnDisplay, gbc);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> insertAccount());

        btnModify.addActionListener(e -> modifyAccount());

        btnDelete.addActionListener(e -> deleteAccount());

        btnDisplay.addActionListener(e -> displayAccounts());

        setTitle("Account Table App");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void connectToDatabase() {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String username = "nagaraju";
        String password = "nagaraju";

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertAccount() {
        String accountNo = txtAccountNo.getText();
        String id = txtId.getText();
        String accountType = txtAccountType.getText();
        String balance = txtBalance.getText();

        try {
            String query = "INSERT INTO account (account_no, id, account_type, balance) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, accountNo);
            statement.setString(2, id);
            statement.setString(3, accountType);
            statement.setString(4, balance);
            statement.executeUpdate();

            clearFields();
            displayAccounts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyAccount() {
        int selectedRow = tblAccounts.getSelectedRow();
        if (selectedRow >= 0) {
            String accountNo = txtAccountNo.getText();
            String id = txtId.getText();
            String accountType = txtAccountType.getText();
            String balance = txtBalance.getText();

            try {
                String query = "UPDATE account SET id=?, account_type=?, balance=? WHERE account_no=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                statement.setString(2, accountType);
                statement.setString(3, balance);
                statement.setString(4, accountNo);
                statement.executeUpdate();

                clearFields();
                displayAccounts();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an account to modify.");
        }
    }

    private void deleteAccount() {
        int selectedRow = tblAccounts.getSelectedRow();
        if (selectedRow >= 0) {
            String accountNo = tblAccounts.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this account?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM account WHERE account_no=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, accountNo);
                    statement.executeUpdate();

                    clearFields();
                    displayAccounts();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an account to delete.");
        }
    }

    private void displayAccounts() {
        try {
            String query = "SELECT * FROM account";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<Account> accounts = new ArrayList<>();
            while (resultSet.next()) {
                String accountNo = resultSet.getString("account_no");
                String id = resultSet.getString("id");
                String accountType = resultSet.getString("account_type");
                String balance = resultSet.getString("balance");
                accounts.add(new Account(accountNo, id, accountType, balance));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Account No", "ID", "Account Type", "Balance"});

            for (Account account : accounts) {
                model.addRow(new String[]{account.getAccountNo(), account.getId(), account.getAccountType(), account.getBalance()});
            }

            tblAccounts.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectAccount() {
        int selectedRow = tblAccounts.getSelectedRow();
        if (selectedRow >= 0) {
            String accountNo = tblAccounts.getValueAt(selectedRow, 0).toString();
            String id = tblAccounts.getValueAt(selectedRow, 1).toString();
            String accountType = tblAccounts.getValueAt(selectedRow, 2).toString();
            String balance = tblAccounts.getValueAt(selectedRow, 3).toString();

            txtAccountNo.setText(accountNo);
            txtId.setText(id);
            txtAccountType.setText(accountType);
            txtBalance.setText(balance);
        }
    }

    private void clearFields() {
        txtAccountNo.setText("");
        txtId.setText("");
        txtAccountType.setText("");
        txtBalance.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AccountTableGUI::new);
    }

    private class Account {
        private String accountNo;
        private String id;
        private String accountType;
       private String balance;

        public Account(String accountNo, String id, String accountType, String balance) {
            this.accountNo = accountNo;
            this.id = id;
            this.accountType = accountType;
            this.balance = balance;
        }

        public String getAccountNo() {
            return accountNo;
        }

        public String getId() {
            return id;
        }

        public String getAccountType() {
            return accountType;
        }

        public String getBalance() {
            return balance;
        }
    }
}
