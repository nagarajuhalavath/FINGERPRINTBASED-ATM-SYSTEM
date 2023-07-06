package fingerprint;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankTableGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField txtBankId, txtBankName, txtBranchAddress, txtContactNo;
    private JTable tblBanks;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public BankTableGUI() {
        initializeUI();
        connectToDatabase();
        displayBanks();
    }

    private void initializeUI() {
        txtBankId = new JTextField();
        txtBankName = new JTextField();
        txtBranchAddress = new JTextField();
        txtContactNo = new JTextField();

        tblBanks = new JTable();
        tblBanks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBanks.getSelectionModel().addListSelectionListener(e -> selectBank());

        JScrollPane scrollPane = new JScrollPane(tblBanks);

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

        panel.add(new JLabel("Bank ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Bank Name:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Branch Address:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Contact No:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtBankId, gbc);
        gbc.gridy++;
        panel.add(txtBankName, gbc);
        gbc.gridy++;
        panel.add(txtBranchAddress, gbc);
        gbc.gridy++;
        panel.add(txtContactNo, gbc);

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

        btnAdd.addActionListener(e -> insertBank());

        btnModify.addActionListener(e -> modifyBank());

        btnDelete.addActionListener(e -> deleteBank());

        btnDisplay.addActionListener(e -> displayBanks());

        setTitle("Bank Table App");
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

    private void insertBank() {
        String bankId = txtBankId.getText();
        String bankName = txtBankName.getText();
        String branchAddress = txtBranchAddress.getText();
        String contactNo = txtContactNo.getText();

        try {
            String query = "INSERT INTO bank (bank_id, bank_name, branch_address, contact_no) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, bankId);
            statement.setString(2, bankName);
            statement.setString(3, branchAddress);
            statement.setString(4, contactNo);
            statement.executeUpdate();

            clearFields();
            displayBanks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyBank() {
        int selectedRow = tblBanks.getSelectedRow();
        if (selectedRow >= 0) {
            String bankId = txtBankId.getText();
            String bankName = txtBankName.getText();
            String branchAddress = txtBranchAddress.getText();
            String contactNo = txtContactNo.getText();

            try {
                String query = "UPDATE bank SET bank_name=?, branch_address=?, contact_no=? WHERE bank_id=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, bankName);
                statement.setString(2, branchAddress);
                statement.setString(3, contactNo);
                statement.setString(4, bankId);
                statement.executeUpdate();

                clearFields();
                displayBanks();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a bank to modify.");
        }
    }

    private void deleteBank() {
        int selectedRow = tblBanks.getSelectedRow();
        if (selectedRow >= 0) {
            String bankId = tblBanks.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this bank?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM bank WHERE bank_id=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, bankId);
                    statement.executeUpdate();

                    clearFields();
                    displayBanks();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a bank to delete.");
        }
    }

    private void displayBanks() {
        try {
            String query = "SELECT * FROM bank";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<Bank> banks = new ArrayList<>();
            while (resultSet.next()) {
                String bankId = resultSet.getString("bank_id");
                String bankName = resultSet.getString("bank_name");
                String branchAddress = resultSet.getString("branch_address");
                String contactNo = resultSet.getString("contact_no");
                banks.add(new Bank(bankId, bankName, branchAddress, contactNo));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Bank ID", "Bank Name", "Branch Address", "Contact No"});

            for (Bank bank : banks) {
                model.addRow(new String[]{bank.getBankId(), bank.getBankName(), bank.getBranchAddress(), bank.getContactNo()});
            }

            tblBanks.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectBank() {
        int selectedRow = tblBanks.getSelectedRow();
        if (selectedRow >= 0) {
            String bankId = tblBanks.getValueAt(selectedRow, 0).toString();
            String bankName = tblBanks.getValueAt(selectedRow, 1).toString();
            String branchAddress = tblBanks.getValueAt(selectedRow, 2).toString();
            String contactNo = tblBanks.getValueAt(selectedRow, 3).toString();

            txtBankId.setText(bankId);
            txtBankName.setText(bankName);
            txtBranchAddress.setText(branchAddress);
            txtContactNo.setText(contactNo);
        }
    }

    private void clearFields() {
        txtBankId.setText("");
        txtBankName.setText("");
        txtBranchAddress.setText("");
        txtContactNo.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankTableGUI::new);
    }

    private class Bank {
        private String bankId;
        private String bankName;
        private String branchAddress;
        private String contactNo;

        public Bank(String bankId, String bankName, String branchAddress, String contactNo) {
            this.bankId = bankId;
            this.bankName = bankName;
            this.branchAddress = branchAddress;
            this.contactNo = contactNo;
        }

        public String getBankId() {
            return bankId;
        }

        public String getBankName() {
            return bankName;
        }

        public String getBranchAddress() {
            return branchAddress;
        }

        public String getContactNo() {
            return contactNo;
        }
    }
}
