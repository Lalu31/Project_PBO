import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class VendingMachineGUI {
    private VendingMachine vendingMachine;
    private JFrame frame;
    private JLabel balanceLabel;
    private Item[] items;
    private CurrencyManager currencyManager;
    private Item selectedItem;
    private JButton buyButton;
    private String selectedCategory;

    public VendingMachineGUI() {
        vendingMachine = new VendingMachine();
        items = new Item[] {
            new Drink("Coke", 15000, 10),
            new Drink("Water", 10000, 5),
            new Drink("Orange Juice", 18000, 7),
            new Drink("Milk", 16000, 8),
            new Drink("Tea", 12000, 6),
            new Drink("Coffee", 20000, 4),
            new Drink("Sprite", 14000, 9),
            new Drink("Buavita", 17000, 5),
            new Drink("Pocari", 19000, 6),
            new Drink("Fanta", 15000, 10),

            new Snack("Chips", 20000, 8),
            new Snack("Candy", 12000, 15),
            new Snack("Chocolate Bar", 18000, 10),
            new Snack("Mie", 7000, 20),
            new Snack("Kacang", 10000, 18),
            new Snack("Biskuit", 15000, 12),
            new Snack("Fitbar", 14000, 10),
            new Snack("Cookies", 16000, 14),
            new Snack("Yogurt", 13000, 6),
            new Snack("Sandwich", 25000, 5),
            new Snack("Salad", 22000, 4)
        };

        frame = new JFrame("Vending Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        showWelcomeScreen(); // Start with the Welcome Screen
    }

    private void setContent(JPanel panel) {
        frame.getContentPane().removeAll();
        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    // Menambahkan metode untuk mendapatkan path gambar dari database
    private String getImagePathFromDatabase() {
        String imagePath = null;
        String query = "SELECT imagePath FROM images WHERE imageName = 'WelcomeImage'"; // Ambil gambar pertama, sesuaikan query jika perlu

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                imagePath = rs.getString("imagePath");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    private void showWelcomeScreen() {
        // Panel utama dengan BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Panel Kiri (untuk gambar)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE); // Ganti dengan warna putih untuk panel kiri

        // Ambil path gambar dari database
        String imagePath = getImagePathFromDatabase(); // Ambil path gambar dari database

        if (imagePath == null) {
            // Jika path tidak ditemukan, gunakan gambar default atau beri pesan error
            imagePath = "";
        }

        // Membuat ImageIcon dengan path gambar
        ImageIcon imageIcon = new ImageIcon(imagePath);

        // Membuat JLabel dengan ImageIcon
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Simulasi gambar dengan border

        // Panel Kanan (untuk teks dan tombol "GO")
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Gunakan FlowLayout
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.LIGHT_GRAY); // Ganti dengan warna abu-abu untuk panel kanan

        // Teks utama di sebelah kanan
        JLabel titleLabel = new JLabel("VENDO SPHERE");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.ITALIC, 30));
        titleLabel.setForeground(Color.BLACK);

        // Teks sambutan di sebelah kanan
        JLabel subtitleLabel = new JLabel("WELCOME BACK");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 24));
        subtitleLabel.setForeground(Color.BLACK);

        // Tombol "GO" di sebelah kanan
        JButton startButton = new JButton("GO");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setFont(new Font("Arial", Font.ITALIC, 12));
        startButton.setBackground(new Color(173, 216, 230)); // Biru muda
        startButton.setOpaque(true);
        startButton.setBorderPainted(true);
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> showCategorySelection());
        startButton.setPreferredSize(new Dimension(150, 30)); // Lebar 150px dan tinggi 50px
        startButton.setMaximumSize(new Dimension(150, 30)); // Membatasi ukuran maksimum tombol

        // Menambahkan elemen ke panel kiri (gambar)
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(imageLabel);
        leftPanel.add(Box.createVerticalGlue());

        // Menambahkan elemen ke panel kanan (teks dan tombol)
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(titleLabel);
        rightPanel.add(Box.createVerticalStrut(20)); // Jarak antara teks utama dan sambutan
        rightPanel.add(subtitleLabel);
        rightPanel.add(Box.createVerticalStrut(40)); // Jarak antara sambutan dan tombol
        rightPanel.add(startButton);
        rightPanel.add(Box.createVerticalGlue());

        // Menambahkan panel kiri dan kanan ke panel utama
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        // Menampilkan panel utama
        setContent(mainPanel);
        frame.setVisible(true);
    }

    private void showCategorySelection() {
        JPanel categoryPanel = new JPanel(new GridLayout(3, 1));
        JLabel label = new JLabel("Select a category:", SwingConstants.CENTER);
        categoryPanel.add(label);

        JButton snackButton = new JButton("Snacks");
        snackButton.addActionListener(e -> {
            selectedCategory = "Snacks";
            showCurrencySelection();
        });
        categoryPanel.add(snackButton);

        JButton drinkButton = new JButton("Drinks");
        drinkButton.addActionListener(e -> {
            selectedCategory = "Drinks";
            showCurrencySelection();
        });
        categoryPanel.add(drinkButton);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> showWelcomeScreen());
        categoryPanel.add(backButton);

        setContent(categoryPanel);
    }

    private void showCurrencySelection() {
        JPanel currencyPanel = new JPanel(new GridLayout(4, 1));
        JLabel label = new JLabel("Choose your currency:", SwingConstants.CENTER);
        currencyPanel.add(label);

        JButton idrButton = new JButton("Rupiah (IDR)");
        idrButton.addActionListener(e -> {
            currencyManager = new CurrencyManager("IDR");
            showItemSelection();
        });
        currencyPanel.add(idrButton);

        JButton usdButton = new JButton("Dollar (USD)");
        usdButton.addActionListener(e -> {
            currencyManager = new CurrencyManager("USD");
            showItemSelection();
        });
        currencyPanel.add(usdButton);

        JButton eurButton = new JButton("Euro (EUR)");
        eurButton.addActionListener(e -> {
            currencyManager = new CurrencyManager("EUR");
            showItemSelection();
        });
        currencyPanel.add(eurButton);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> showCategorySelection());
        currencyPanel.add(backButton);

        setContent(currencyPanel);
    }

    private void showItemSelection() {
        JPanel itemPanel = new JPanel(new BorderLayout());
        JPanel productPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        JLabel titleLabel = new JLabel("Select an item:", SwingConstants.CENTER);

        for (Item item : items) {
            if ((selectedCategory.equals("Snacks") && item instanceof Snack) ||
                (selectedCategory.equals("Drinks") && item instanceof Drink)) {
                JButton itemButton = new JButton(String.format("%s - %.2f %s (%d)", item.getName(),
                        currencyManager.convertFromRupiah(item.getPrice()),
                        currencyManager.getSelectedCurrency(),
                        item.getStock()));
                itemButton.addActionListener(e -> {
                    selectedItem = item;
                    itemButton.setBackground(Color.GREEN);
                    for (Component c : productPanel.getComponents()) {
                        if (c instanceof JButton && c != itemButton) {
                            c.setBackground(UIManager.getColor("Button.background"));
                        }
                    }
                });
                productPanel.add(itemButton);
            }
        }

        itemPanel.add(titleLabel, BorderLayout.NORTH);
        itemPanel.add(new JScrollPane(productPanel), BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> showCurrencySelection());
        itemPanel.add(backButton, BorderLayout.SOUTH);

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(e -> showInputMoney());
        itemPanel.add(nextButton, BorderLayout.EAST);

        setContent(itemPanel);
    }

    private void showInputMoney() {
        JPanel moneyPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(4, 3, 5, 5));
        balanceLabel = new JLabel("Balance: 0 " + currencyManager.getSelectedCurrency());
        moneyPanel.add(balanceLabel, BorderLayout.NORTH);

        JTextField amountField = new JTextField();
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                double convertedAmount = currencyManager.convertToRupiah(amount);
                vendingMachine.addBalance(convertedAmount);
                updateBalance();
                amountField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid amount!");
            }
        });
        JButton deleteButton = new JButton("<--");
        deleteButton.addActionListener(e -> {
            String text = amountField.getText();
            if (text.length() > 0) {
                amountField.setText(text.substring(0, text.length() - 1));
            }
        });

        for (int i = 1; i <= 9; i++) {
            JButton button = new JButton(String.valueOf(i));
            button.addActionListener(e -> amountField.setText(amountField.getText() + e.getActionCommand()));
            inputPanel.add(button);
        }
        inputPanel.add(addButton);
        inputPanel.add(new JButton("0"));
        inputPanel.add(deleteButton);

        moneyPanel.add(inputPanel, BorderLayout.CENTER);

        buyButton = new JButton("Buy");
        buyButton.addActionListener(e -> startDispensingProcess());
        moneyPanel.add(buyButton, BorderLayout.SOUTH);

        // Add "Back" button to return to the item selection screen
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> showItemSelection());
        moneyPanel.add(backButton, BorderLayout.WEST);

        // Add "Finish" button to complete the transaction
        JButton finishButton = new JButton("Finish");
        finishButton.addActionListener(e -> finishTransaction());
        moneyPanel.add(finishButton, BorderLayout.EAST);

        setContent(moneyPanel);
    }

    private void startDispensingProcess() {
        if (selectedItem == null || vendingMachine.getBalance() < selectedItem.getPrice()) {
            JOptionPane.showMessageDialog(frame, "Insufficient balance or no item selected!");
            return;
        }

        // Simulate dispensing process with a loading dialog
        JDialog loadingDialog = new JDialog(frame, "Processing Order", true);
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(true); // Set the progress bar as indeterminate
        loadingDialog.add(new JLabel("Processing your order..."), BorderLayout.NORTH);
        loadingDialog.add(progressBar, BorderLayout.CENTER);
        loadingDialog.setSize(300, 100);
        loadingDialog.setLocationRelativeTo(frame);

        // Start a background task to simulate the dispensing process
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    Thread.sleep(2000); // Simulate a 2-second processing time
                    vendingMachine.dispenseItem(selectedItem); // Dispense the item
                    return null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void done() {
                loadingDialog.dispose(); // Close the loading dialog
                JOptionPane.showMessageDialog(frame, "Product dispensed successfully!");
                updateBalance();
            }
        };

        worker.execute();
        loadingDialog.setVisible(true);
    }

    private void finishTransaction() {
        double remainingBalance = vendingMachine.getBalance();
        if (remainingBalance > 0) {
            // Refund remaining balance to the selected currency
            double refundedAmount = currencyManager.convertFromRupiah(remainingBalance);
            JOptionPane.showMessageDialog(frame, "Your remaining balance of " + refundedAmount + " " +
                    currencyManager.getSelectedCurrency() + " will be refunded.");
        }

        // Reset the vending machine (clear balance and item stock)
        vendingMachine.reset();

        // Go back to the Welcome Screen
        showWelcomeScreen();
    }

    private void updateBalance() {
        double balance = vendingMachine.getBalance();
        balanceLabel.setText("Balance: " + String.format("%.2f", currencyManager.convertFromRupiah(balance)) +
                             " " + currencyManager.getSelectedCurrency());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VendingMachineGUI::new);
    }
}