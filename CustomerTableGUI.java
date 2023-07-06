package fingerprint;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomerTableGUI extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtId, txtName, txtAddress, txtContactNo, txtAadharNo, txtFingerprint, txtPin;
    private JTable tblCustomers;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;
    private Random random;

    public CustomerTableGUI() {
        initializeUI();
        connectToDatabase();
        random = new Random();
        displayCustomers();
    }

    private void initializeUI() {
        txtId = new JTextField();
        txtName = new JTextField();
        txtAddress = new JTextField();
        txtContactNo = new JTextField();
        txtAadharNo = new JTextField();
        txtFingerprint = new JTextField();
        txtPin = new JTextField();

        tblCustomers = new JTable();
        tblCustomers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCustomers.getSelectionModel().addListSelectionListener(e -> selectCustomer());

        JScrollPane scrollPane = new JScrollPane(tblCustomers);

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

        panel.add(new JLabel("ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Contact No:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Aadhar No:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Fingerprint:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("PIN:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtId, gbc);
        gbc.gridy++;
        panel.add(txtName, gbc);
        gbc.gridy++;
        panel.add(txtAddress, gbc);
        gbc.gridy++;
        panel.add(txtContactNo, gbc);
        gbc.gridy++;
        panel.add(txtAadharNo, gbc);
        gbc.gridy++;
        panel.add(txtFingerprint, gbc);
        gbc.gridy++;
        panel.add(txtPin, gbc);

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

        btnAdd.addActionListener(e -> insertCustomer());

        btnModify.addActionListener(e -> modifyCustomer());

        btnDelete.addActionListener(e -> deleteCustomer());

        btnDisplay.addActionListener(e -> displayCustomers());

        setTitle("Customer Table App");
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

private void insertCustomer() {
    String id = txtId.getText();
    String name = txtName.getText();
    String address = txtAddress.getText();
    String contactNo = txtContactNo.getText();
    String aadharNo = txtAadharNo.getText();
    String fingerprint = generateRandomFingerprint();
    String pin = txtPin.getText();

    try {
        String query = "INSERT INTO customer (id, name, address, contact_no, aadhar_no, fingerprint, pin) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, id);
        statement.setString(2, name);
        statement.setString(3, address);
        statement.setString(4, contactNo);
        statement.setString(5, aadharNo);
        statement.setString(6, fingerprint);
        statement.setString(7, pin);
        statement.executeUpdate();

        clearFields();
        displayCustomers();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private String generateRandomFingerprint() {
    StringBuilder fingerprint = new StringBuilder();
    for (int i = 0; i < 10; i++) {
        fingerprint.append(random.nextInt(10));
    }
    return fingerprint.toString();
}

private void modifyCustomer() {
    int selectedRow = tblCustomers.getSelectedRow();
    if (selectedRow >= 0) {
        String id = txtId.getText();
        String name = txtName.getText();
        String address = txtAddress.getText();
        String contactNo = txtContactNo.getText();
        String aadharNo = txtAadharNo.getText();
        String fingerprint = txtFingerprint.getText();
        String pin = txtPin.getText();

        try {
            String query = "UPDATE customer SET name=?, address=?, contact_no=?, aadhar_no=?, fingerprint=?, pin=? WHERE id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, address);
            statement.setString(3, contactNo);
            statement.setString(4, aadharNo);
            statement.setString(5, fingerprint);
            statement.setString(6, pin);
            statement.setString(7, id);
            statement.executeUpdate();

            clearFields();
            displayCustomers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select a customer to modify.");
    }
}

private void deleteCustomer() {
    int selectedRow = tblCustomers.getSelectedRow();
    if (selectedRow >= 0) {
        String id = tblCustomers.getValueAt(selectedRow, 0).toString();

        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM customer WHERE id=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, id);
                statement.executeUpdate();

                clearFields();
                displayCustomers();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select a customer to delete.");
    }
}

private void displayCustomers() {
    try {
        String query = "SELECT * FROM customer";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        List<Customer> customers = new ArrayList<>();
        while (resultSet.next()) {
            String id = resultSet.getString("id");
            String name = resultSet.getString("name");
            String address = resultSet.getString("address");
            String contactNo = resultSet.getString("contact_no");
            String aadharNo = resultSet.getString("aadhar_no");
            String fingerprint = resultSet.getString("fingerprint");
            String pin = resultSet.getString("pin");
            customers.add(new Customer(id, name, address, contactNo, aadharNo, fingerprint, pin));
        }

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Name", "Address", "Contact No", "Aadhar No", "Fingerprint", "PIN"});

        for (Customer customer : customers) {
            model.addRow(new String[]{customer.getId(), customer.getName(), customer.getAddress(),
                    customer.getContactNo(), customer.getAadharNo(), customer.getFingerprint(), customer.getPin()});
        }

        tblCustomers.setModel(model);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private void selectCustomer() {
    int selectedRow = tblCustomers.getSelectedRow();
    if (selectedRow >= 0) {
        String id = tblCustomers.getValueAt(selectedRow, 0).toString();
        String name = tblCustomers.getValueAt(selectedRow, 1).toString();
        String address = tblCustomers.getValueAt(selectedRow, 2).toString();
        String contactNo = tblCustomers.getValueAt(selectedRow, 3).toString();
        String aadharNo = tblCustomers.getValueAt(selectedRow, 4).toString();
        String fingerprint = tblCustomers.getValueAt(selectedRow, 5).toString();
        String pin = tblCustomers.getValueAt(selectedRow, 6).toString();

        txtId.setText(id);
        txtName.setText(name);
        txtAddress.setText(address);
        txtContactNo.setText(contactNo);
        txtAadharNo.setText(aadharNo);
        txtFingerprint.setText(fingerprint);
        txtPin.setText(pin);
    }
}

private void clearFields() {
    txtId.setText("");
    txtName.setText("");
    txtAddress.setText("");
    txtContactNo.setText("");
    txtAadharNo.setText("");
    txtFingerprint.setText("");
    txtPin.setText("");
}

public static void main(String[] args) {
    SwingUtilities.invokeLater(CustomerTableGUI::new);
}

private class Customer {
    private String id;
    private String name;
    private String address;
    private String contactNo;
    private String aadharNo;
    private String fingerprint;
    private String pin;

    public Customer(String id, String name, String address, String contactNo, String aadharNo, String fingerprint, String pin) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.contactNo = contactNo;
        this.aadharNo = aadharNo;
        this.fingerprint = fingerprint;
        this.pin = pin;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getAadharNo() {
        return aadharNo;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public String getPin() {
        return pin;
    }
}
}
