package fingerprint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainPage extends JFrame {
    /*
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private JButton retrieveMarksButton;

    public MainPage() {
        // Set frame properties
        setTitle("Fingerprint Based ATM system");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create label
        JLabel welcomeLabel = new JLabel("Fingerprint Based ATM system");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(welcomeLabel, BorderLayout.NORTH);

        // Create panel for the button
        /*JPanel buttonPanel = new JPanel();
        retrieveMarksButton = new JButton("Retrieve Marks");
        buttonPanel.add(retrieveMarksButton);
*/
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create menus
        JMenu customerMenu = new JMenu("customer Details");
        JMenu accountMenu = new JMenu(" account Details");
        JMenu transactionMenu = new JMenu("transaction Details");
        JMenu atmMenu = new JMenu("atm Details");
        JMenu bankMenu = new JMenu("bank Details");

        // Create menu item for student menu
        JMenuItem viewcustomerDetails = new JMenuItem("View customer Details");
        viewcustomerDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CustomerTableGUI();
            }
        });

        // Create menu item for course menu
        JMenuItem viewaccountDetails = new JMenuItem("View account Details");
        viewaccountDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AccountTableGUI();
            }
        });

        // Create menu item for enrollment menu
        JMenuItem viewtransactionDetails = new JMenuItem("View transaction Details");
        viewtransactionDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new TransactionTableGUI();
            }
        });

        // Create menu item for semester menu
        JMenuItem viewatmDetails = new JMenuItem("View atm Details");
        viewatmDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AtmTableGUI();
            }
        });

        // Create menu item for grade menu
        JMenuItem viewbankDetails = new JMenuItem("View bank Details");
        viewbankDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new BankTableGUI();
            }
        });

        // Add menu items to respective menus
        customerMenu.add(viewcustomerDetails);
        accountMenu.add(viewaccountDetails);
        transactionMenu.add(viewtransactionDetails);
        atmMenu.add(viewatmDetails);
        bankMenu.add(viewbankDetails);

        // Add menus to the menu bar
        menuBar.add(customerMenu);
        menuBar.add(accountMenu);
        menuBar.add(transactionMenu);
        menuBar.add(atmMenu);
        menuBar.add(bankMenu);

        // Set the menu bar
        setJMenuBar(menuBar);

        // Add the button panel to the frame
       // add(buttonPanel, BorderLayout.CENTER);

        // Set button action for "Retrieve Marks"
       
        /*retrieveMarksButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Retreive();
            }
        });*/

        // Add window listener to handle maximizing the window
        addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                    System.out.println("Window maximized");
                } else {
                    System.out.println("Window not maximized");
                }
            }
        });

        // Set frame size and visibility
        setSize(800, 600);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainPage();
    }
}