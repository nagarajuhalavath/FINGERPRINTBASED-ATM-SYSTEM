package fingerprint;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionTableGUI extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtTransactionId, txtAccountNo, txtTransactionType, txtTransactionAmount, txtAtmId;
    private JTable tblTransactions;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public TransactionTableGUI() {
        initializeUI();
        connectToDatabase();
        displayTransactions();
    }

    private void initializeUI() {
        txtTransactionId = new JTextField();
        txtAccountNo = new JTextField();
        txtTransactionType = new JTextField();
        txtTransactionAmount = new JTextField();
        txtAtmId = new JTextField();

        tblTransactions = new JTable();
        tblTransactions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTransactions.getSelectionModel().addListSelectionListener(e -> selectTransaction());

        JScrollPane scrollPane = new JScrollPane(tblTransactions);

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

        panel.add(new JLabel("Transaction ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Account No:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Transaction Type:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Transaction Amount:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("ATM ID:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtTransactionId, gbc);
        gbc.gridy++;
        panel.add(txtAccountNo, gbc);
        gbc.gridy++;
        panel.add(txtTransactionType, gbc);
        gbc.gridy++;
        panel.add(txtTransactionAmount, gbc);
        gbc.gridy++;
        panel.add(txtAtmId, gbc);

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

        btnAdd.addActionListener(e -> insertTransaction());

        btnModify.addActionListener(e -> modifyTransaction());

        btnDelete.addActionListener(e -> deleteTransaction());

        btnDisplay.addActionListener(e -> displayTransactions());

        setTitle("Transaction App");
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

    private void insertTransaction() {
        String transactionId = txtTransactionId.getText();
        String accountNo = txtAccountNo.getText();
        String transactionType = txtTransactionType.getText();
        String transactionAmount = txtTransactionAmount.getText();
        String atmId = txtAtmId.getText();

        try {
            String query = "INSERT INTO transaction (transaction_id, account_no, transaction_type, transaction_amount, atm_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, transactionId);
            statement.setString(2, accountNo);
            statement.setString(3, transactionType);
            statement.setString(4, transactionAmount);
            statement.setString(5, atmId);
            statement.executeUpdate();

            clearFields();
            displayTransactions();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyTransaction() {
        int selectedRow = tblTransactions.getSelectedRow();
        if (selectedRow >= 0) {
            String transactionId = txtTransactionId.getText();
            String accountNo = txtAccountNo.getText();
            String transactionType = txtTransactionType.getText();
            String transactionAmount = txtTransactionAmount.getText();
            String atmId = txtAtmId.getText();

            try {
                String query = "UPDATE transaction SET account_no=?, transaction_type=?, transaction_amount=?, atm_id=? WHERE transaction_id=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, accountNo);
                statement.setString(2, transactionType);
                statement.setString(3, transactionAmount);
                statement.setString(4, atmId);
                statement.setString(5, transactionId);
                statement.executeUpdate();

                clearFields();
                displayTransactions();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a transaction to modify.");
        }
    }

    private void deleteTransaction() {
        int selectedRow = tblTransactions.getSelectedRow();
        if (selectedRow >= 0) {
            String transactionId = tblTransactions.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this transaction?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM transaction WHERE transaction_id=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, transactionId);
                    statement.executeUpdate();

                    clearFields();
                    displayTransactions();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a transaction to delete.");
        }
    }

    private void displayTransactions() {
        try {
            String query = "SELECT * FROM transaction";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<Transaction> transactions = new ArrayList<>();
            while (resultSet.next()) {
                String transactionId = resultSet.getString("transaction_id");
                String accountNo = resultSet.getString("account_no");
                String transactionType = resultSet.getString("transaction_type");
                String transactionAmount = resultSet.getString("transaction_amount");
                String atmId = resultSet.getString("atm_id");
                transactions.add(new Transaction(transactionId, accountNo, transactionType, transactionAmount, atmId));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Transaction ID", "Account No", "Transaction Type", "Transaction Amount", "ATM ID"});

            for (Transaction transaction : transactions) {
                model.addRow(new String[]{transaction.getTransactionId(), transaction.getAccountNo(), transaction.getTransactionType(), transaction.getTransactionAmount(), transaction.getAtmId()});
            }

            tblTransactions.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectTransaction() {
        int selectedRow = tblTransactions.getSelectedRow();
        if (selectedRow >= 0) {
            String transactionId =tblTransactions.getValueAt(selectedRow, 0).toString();
            String accountNo = tblTransactions.getValueAt(selectedRow, 1).toString();
            String transactionType = tblTransactions.getValueAt(selectedRow, 2).toString();
            String transactionAmount = tblTransactions.getValueAt(selectedRow, 3).toString();
            String atmId = tblTransactions.getValueAt(selectedRow, 4).toString();

            txtTransactionId.setText(transactionId);
            txtAccountNo.setText(accountNo);
            txtTransactionType.setText(transactionType);
            txtTransactionAmount.setText(transactionAmount);
            txtAtmId.setText(atmId);
        }
    }

    private void clearFields() {
        txtTransactionId.setText("");
        txtAccountNo.setText("");
        txtTransactionType.setText("");
        txtTransactionAmount.setText("");
        txtAtmId.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TransactionTableGUI::new);
    }

    private class Transaction {
        private String transactionId;
        private String accountNo;
        private String transactionType;
        private String transactionAmount;
        private String atmId;

        public Transaction(String transactionId, String accountNo, String transactionType, String transactionAmount, String atmId) {
            this.transactionId = transactionId;
            this.accountNo = accountNo;
            this.transactionType = transactionType;
            this.transactionAmount = transactionAmount;
            this.atmId = atmId;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getAccountNo() {
            return accountNo;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public String getTransactionAmount() {
            return transactionAmount;
        }

        public String getAtmId() {
            return atmId;
        }
    }
}
