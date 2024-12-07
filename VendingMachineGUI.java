import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
    private List<Item> selectedItems = new ArrayList<>();  // Menyimpan item yang dipilih
    private JLabel itemNameLabel;  // Deklarasi label untuk menampilkan nama item yang dipilih

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
        rightPanel.setBackground(new Color(214, 207, 194)); // Ganti dengan warna coklat

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
        startButton.setBackground(new Color(183, 155, 113)); // Coklat tua
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
        // Panel utama dengan BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
    
        // Panel Kiri (untuk gambar)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
    
        // Ambil path gambar dari database
        String imagePath = getImagePathFromDatabase();
    
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
    
        // Menambahkan elemen ke panel kiri
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(imageLabel);
        leftPanel.add(Box.createVerticalGlue());
    
        // Panel Kanan (untuk teks dan tombol)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(214, 207, 194));
    
        // Label untuk kategori
        JLabel categoryLabel = new JLabel("Select a category:");
        categoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        categoryLabel.setFont(new Font("Arial", Font.ITALIC, 24));
        categoryLabel.setForeground(Color.BLACK);
    
        // Tombol kategori "Snacks"
        JButton snackButton = new JButton("Snacks");
        styleButton(snackButton, new Color(183, 155, 113)); // Warna Coklat Tua
        snackButton.addActionListener(e -> {
            selectedCategory = "Snacks";
            showCurrencySelection();
        });
    
        // Tombol kategori "Drinks"
        JButton drinkButton = new JButton("Drinks");
        styleButton(drinkButton, new Color(183, 155, 113)); 
        drinkButton.addActionListener(e -> {
            selectedCategory = "Drinks";
            showCurrencySelection();
        });
    
        // Tombol "Back"
        JButton backButton = new JButton("Back");
        styleButton(backButton, new Color(183, 155, 113)); 
        backButton.addActionListener(e -> showWelcomeScreen());
    
        // Menambahkan elemen ke panel kanan
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(categoryLabel);
        rightPanel.add(Box.createVerticalStrut(20)); // Jarak antara label dan tombol pertama
        rightPanel.add(snackButton);
        rightPanel.add(Box.createVerticalStrut(20)); // Jarak antara tombol-tombol
        rightPanel.add(drinkButton);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(backButton);
        rightPanel.add(Box.createVerticalGlue());
    
        // Menambahkan panel kiri dan kanan ke panel utama
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
    
        // Menampilkan panel utama
        setContent(mainPanel);
    }
    
    // Metode untuk styling tombol
    private void styleButton(JButton button, Color backgroundColor) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.ITALIC, 18));
        button.setBackground(backgroundColor);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40)); // Ukuran tombol
        button.setMaximumSize(new Dimension(150, 40));   // Ukuran maksimum tombol
    }    

    private void showCurrencySelection() {
        // Panel utama dengan BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
    
        // Panel Kiri (untuk gambar)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
    
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
    
        // Menambahkan elemen ke panel kiri
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(imageLabel);
        leftPanel.add(Box.createVerticalGlue());
    
        // Panel Kanan (untuk teks dan tombol)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(214, 207, 194)); // Warna background panel kanan
    
        // Label untuk kategori
        JLabel currencyLabel = new JLabel("Choose your currency:");
        currencyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        currencyLabel.setFont(new Font("Arial", Font.ITALIC, 24));
        currencyLabel.setForeground(Color.BLACK);
    
        // Tombol untuk memilih Rupiah (IDR)
        JButton idrButton = new JButton("Rupiah (IDR)");
        styleButton2(idrButton, new Color(183, 155, 113)); // Coklat tua
        idrButton.addActionListener(e -> {
            currencyManager = new CurrencyManager("IDR");
            showItemSelection(); // Arahkan ke pemilihan item setelah memilih mata uang
        });
    
        // Tombol untuk memilih Dollar (USD)
        JButton usdButton = new JButton("Dollar (USD)");
        styleButton2(usdButton, new Color(183, 155, 113)); // Coklat tua
        usdButton.addActionListener(e -> {
            currencyManager = new CurrencyManager("USD");
            showItemSelection(); // Arahkan ke pemilihan item setelah memilih mata uang
        });
    
        // Tombol untuk memilih Euro (EUR)
        JButton eurButton = new JButton("Euro (EUR)");
        styleButton2(eurButton, new Color(183, 155, 113)); // Coklat tua
        eurButton.addActionListener(e -> {
            currencyManager = new CurrencyManager("EUR");
            showItemSelection(); // Arahkan ke pemilihan item setelah memilih mata uang
        });
    
        // Tombol "Back"
        JButton backButton = new JButton("Back");
        styleButton(backButton, new Color(183, 155, 113)); // Coklat tua
        backButton.addActionListener(e -> showCategorySelection()); // Kembali ke kategori
    
        // Menambahkan elemen ke panel kanan
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(currencyLabel);
        rightPanel.add(Box.createVerticalStrut(20)); // Jarak antara label dan tombol pertama
        rightPanel.add(idrButton);
        rightPanel.add(Box.createVerticalStrut(20)); // Jarak antara tombol-tombol
        rightPanel.add(usdButton);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(eurButton);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(backButton);
        rightPanel.add(Box.createVerticalGlue());
    
        // Menambahkan panel kiri dan kanan ke panel utama
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
    
        // Menampilkan panel utama
        setContent(mainPanel);
    }
    
    // Metode untuk styling tombol
    private void styleButton2(JButton button, Color backgroundColor) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.ITALIC, 18));
        button.setBackground(backgroundColor);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40)); // Ukuran tombol
        button.setMaximumSize(new Dimension(150, 40));   // Ukuran maksimum tombol
    }    

    private void showItemSelection() {
        // Panel utama dengan BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
    
        // Panel Kiri untuk tombol-tombol item dengan layout 2 kolom
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(0, 5, 10, 10)); // 5 tombol per baris
        leftPanel.setBackground(new Color(240, 240, 240)); // Panel kiri dengan warna RGB(240, 240, 240)
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Menghilangkan border di panel kiri
    
        // Menambahkan tombol untuk setiap item sesuai kategori yang dipilih
        for (Item item : items) {
            if ((selectedCategory.equals("Snacks") && item instanceof Snack) ||
                (selectedCategory.equals("Drinks") && item instanceof Drink)) {
                JButton itemButton = new JButton(String.format("%s - %.2f %s (%d)", item.getName(),
                       currencyManager.convertFromRupiah(item.getPrice()),
                       currencyManager.getSelectedCurrency(),
                       item.getStock()));
                itemButton.setFont(new Font("Arial", Font.PLAIN, 16));
    
                // Menandai tombol dengan warna hijau jika item dipilih
                if (selectedItems.contains(item)) {
                    itemButton.setBackground(Color.GREEN);
                }
    
                itemButton.addActionListener(e -> {
                    // Memilih item dan menambahkannya ke daftar item yang dipilih
                    if (!selectedItems.contains(item)) {
                        selectedItems.add(item);
                        itemButton.setBackground(Color.GREEN); // Menandai item yang dipilih
                    } else {
                        selectedItems.remove(item);
                        itemButton.setBackground(UIManager.getColor("Button.background")); // Mengembalikan warna default
                    }
    
                    // Memperbarui label di panel kanan untuk menampilkan semua item yang dipilih
                    updateSelectedItemsLabel();
                });
                leftPanel.add(itemButton); // Menambahkan tombol item ke panel kiri
            }
        }
    
        // Panel Kanan untuk label dan tombol Next dan Back
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS)); // BoxLayout untuk tombol Next dan Back
        rightPanel.setBackground(new Color(214, 207, 194)); // Panel kanan dengan warna latar belakang RGB(214, 207, 194)
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Menghilangkan border di panel kanan
    
        // Label untuk judul
        JLabel titleLabel = new JLabel("Select an item", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.ITALIC, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        // Label untuk menampilkan semua item yang dipilih
        itemNameLabel = new JLabel("List: ", SwingConstants.CENTER);
        itemNameLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        itemNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        // Tombol Back
        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setFont(new Font("Arial", Font.ITALIC, 18));
        backButton.setBackground(new Color(183, 155, 113)); // Warna coklat tua
        backButton.setOpaque(true);
        backButton.setBorderPainted(true);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> showCurrencySelection());
    
        // Tombol Next
        JButton nextButton = new JButton("Next");
        nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextButton.setFont(new Font("Arial", Font.ITALIC, 18));
        nextButton.setBackground(new Color(183, 155, 113)); // Warna coklat tua
        nextButton.setOpaque(true);
        nextButton.setBorderPainted(true);
        nextButton.setFocusPainted(false);
        nextButton.addActionListener(e -> showInputMoney());
    
        // Menambahkan label dan tombol ke panel kanan
        rightPanel.add(Box.createVerticalGlue()); // Menambahkan ruang vertikal di atas tombol
        rightPanel.add(titleLabel);
        rightPanel.add(Box.createVerticalStrut(20)); // Jarak antara label dan tombol pertama
        rightPanel.add(itemNameLabel); // Menambahkan label untuk nama item yang dipilih
        rightPanel.add(Box.createVerticalStrut(20)); // Jarak antara label dan tombol berikutnya
        rightPanel.add(backButton);
        rightPanel.add(Box.createVerticalStrut(20)); // Jarak antar tombol
        rightPanel.add(nextButton);
        rightPanel.add(Box.createVerticalGlue()); // Menambahkan ruang vertikal di bawah tombol
    
        // Panel kiri dengan lebar dinamis berdasarkan konten
        leftPanel.setPreferredSize(new Dimension(1100, mainPanel.getHeight()));  // Lebar panel kiri yang sesuai
    
        // Panel kanan dengan lebar tetap dan memberikan sedikit margin agar lebih seimbang
        rightPanel.setPreferredSize(new Dimension(350, mainPanel.getHeight())); // Panel kanan lebih lebar dari sebelumnya
    
        // Menambahkan panel kiri dan kanan ke panel utama
        mainPanel.add(leftPanel, BorderLayout.WEST);  // Panel kiri untuk tombol item
        mainPanel.add(rightPanel, BorderLayout.EAST); // Panel kanan untuk label, tombol Next dan Back
    
        // Memperbarui label item yang dipilih ketika kembali ke halaman
        updateSelectedItemsLabel(); // Memperbarui label daftar item yang dipilih sebelum menampilkan panel
    
        // Menampilkan panel utama
        setContent(mainPanel);
    }
    
    // Fungsi untuk memperbarui label yang menampilkan semua item yang dipilih
    private void updateSelectedItemsLabel() {
    StringBuilder selectedItemsText = new StringBuilder("List: ");
        for (Item item : selectedItems) {
            selectedItemsText.append(item.getName()).append(", ");
        }
        if (selectedItems.size() > 0) {
            selectedItemsText.setLength(selectedItemsText.length() - 2); // Menghapus koma terakhir
        } else {
            selectedItemsText.append("None");
        }
        itemNameLabel.setText(selectedItemsText.toString()); // Memperbarui label
    }

    private void showInputMoney() {
        // Panel utama untuk input uang
        JPanel moneyPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(4, 3, 5, 5));
    
        // Field untuk memasukkan nilai uang
        JTextField amountField = new JTextField();
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            try {
                String input = amountField.getText().trim();
                if (input.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Amount cannot be empty!");
                    return;
                }
                double amount = Double.parseDouble(input);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(frame, "Amount must be greater than 0!");
                    return;
                }
                double convertedAmount = currencyManager.convertToRupiah(amount);
                vendingMachine.addBalance(convertedAmount); // Saldo diperbarui
                updateBalance(); // Memperbarui tampilan saldo
                amountField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid amount format!");
            }
        });
    
        JButton deleteButton = new JButton("<--");
        deleteButton.addActionListener(e -> {
            String text = amountField.getText();
            if (!text.isEmpty()) {
                amountField.setText(text.substring(0, text.length() - 1));
            }
        });
    
        // Membuat tombol angka 1-9
        for (int i = 1; i <= 9; i++) {
            JButton button = new JButton(String.valueOf(i));
            button.addActionListener(e -> amountField.setText(amountField.getText() + button.getText()));
            inputPanel.add(button);
        }
    
        // Tombol Add di kolom pertama pada baris terakhir
        inputPanel.add(addButton);
    
        // Tombol 0 di kolom kedua pada baris terakhir
        JButton zeroButton = new JButton("0");
        zeroButton.addActionListener(e -> amountField.setText(amountField.getText() + "0"));
        inputPanel.add(zeroButton);
    
        // Tombol delete (<--) di kolom ketiga pada baris terakhir
        inputPanel.add(deleteButton);
    
        // Panel kanan untuk label dan tombol
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(214, 207, 194));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Menambah margin lebih besar
        rightPanel.setPreferredSize(new Dimension(300, 0)); // Lebar diperbesar menjadi 300px
    
        // Label untuk menampilkan saldo
        balanceLabel = new JLabel("", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateBalance(); // Memperbarui saldo saat halaman ditampilkan
    
        // Menampilkan daftar item yang dipilih
        JLabel selectedItemsLabel = new JLabel("Selected Items: ", SwingConstants.CENTER);
        selectedItemsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        selectedItemsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        StringBuilder selectedItemsText = new StringBuilder("None");
        if (!selectedItems.isEmpty()) {
            selectedItemsText.setLength(0);
            for (Item item : selectedItems) {
                selectedItemsText.append(item.getName()).append(", ");
            }
            selectedItemsText.setLength(selectedItemsText.length() - 2); // Menghapus koma terakhir
        }
        selectedItemsLabel.setText("Selected Items: " + selectedItemsText);
    
        // Menambahkan label untuk menampilkan total harga
        JLabel totalPriceLabel = new JLabel("Total Price: 0.00", SwingConstants.CENTER);
        totalPriceLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        totalPriceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        // Fungsi untuk memperbarui total harga
        updateTotalPriceLabel(totalPriceLabel);
    
        // Membuat tombol dengan ukuran seragam
        Dimension buttonSize = new Dimension(120, 30);
    
        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setPreferredSize(buttonSize);
        backButton.setMaximumSize(buttonSize);  // Menambahkan untuk memastikan ukuran tombol tetap seragam
        backButton.setFont(new Font("Arial", Font.ITALIC, 18));
        backButton.setBackground(new Color(183, 155, 113));
        backButton.setOpaque(true);
        backButton.setBorderPainted(true);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> showItemSelection());
    
        JButton buyButton = new JButton("Buy");
        buyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buyButton.setPreferredSize(buttonSize);
        buyButton.setMaximumSize(buttonSize);  // Menambahkan untuk memastikan ukuran tombol tetap seragam
        buyButton.setFont(new Font("Arial", Font.ITALIC, 18));
        buyButton.setBackground(new Color(183, 155, 113));
        buyButton.setOpaque(true);
        buyButton.setBorderPainted(true);
        buyButton.setFocusPainted(false);
        buyButton.addActionListener(e -> startDispensingProcess(selectedItems));
    
        JButton finishButton = new JButton("Finish");
        finishButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        finishButton.setPreferredSize(buttonSize);
        finishButton.setMaximumSize(buttonSize);  // Menambahkan untuk memastikan ukuran tombol tetap seragam
        finishButton.setFont(new Font("Arial", Font.ITALIC, 18));
        finishButton.setBackground(new Color(183, 155, 113));
        finishButton.setOpaque(true);
        finishButton.setBorderPainted(true);
        finishButton.setFocusPainted(false);
        finishButton.addActionListener(e -> finishTransaction());
    
        // Menambahkan komponen ke panel kanan
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(balanceLabel);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(selectedItemsLabel);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(totalPriceLabel); // Menambahkan label total harga
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(backButton);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(buyButton);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(finishButton);
        rightPanel.add(Box.createVerticalGlue());
    
        // Menambahkan panel input dan panel kanan ke panel utama
        moneyPanel.add(inputPanel, BorderLayout.CENTER);
        moneyPanel.add(rightPanel, BorderLayout.EAST);
    
        setContent(moneyPanel); // Menampilkan panel
    }
    
    // Fungsi untuk memperbarui total harga
    private void updateTotalPriceLabel(JLabel totalPriceLabel) {
        double totalPrice = 0;
        for (Item item : selectedItems) {
            totalPrice += item.getPrice();
        }
        totalPriceLabel.setText("Total Price: " + currencyManager.convertFromRupiah(totalPrice) + " " + currencyManager.getSelectedCurrency());
    }
    
    
    private void startDispensingProcess(List<Item> selectedItems) {
        double totalCost = 0;
        for (Item item : selectedItems) {
            totalCost += item.getPrice(); // Hitung total harga untuk semua item yang dipilih
        }

        if (vendingMachine.getBalance() < totalCost) {
            JOptionPane.showMessageDialog(frame, "Insufficient balance!");
            return;
        }

        // Simulate dispensing process
        JDialog loadingDialog = new JDialog(frame, "Processing Order", true);
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(true); // Set the progress bar as indeterminate
        loadingDialog.add(new JLabel("Processing your order..."), BorderLayout.NORTH);
        loadingDialog.add(progressBar, BorderLayout.CENTER);
        loadingDialog.setSize(300, 100);
        loadingDialog.setLocationRelativeTo(frame);

        // Start background task to simulate the dispensing process
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    Thread.sleep(2000); // Simulate a 2-second processing time
                    for (Item item : selectedItems) {
                        vendingMachine.dispenseItem(item); // Dispense each selected item
                    }
                    return null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void done() {
                loadingDialog.dispose(); // Close the loading dialog
                JOptionPane.showMessageDialog(frame, "Products dispensed successfully!");
                updateBalance();
            }
        };

        worker.execute();
        loadingDialog.setVisible(true);
    } 


    private void finishTransaction() {
        double remainingBalance = vendingMachine.getBalance();
        
        // Menampilkan sisa saldo jika ada
        if (remainingBalance > 0) {
            // Refund remaining balance to the selected currency
            double refundedAmount = currencyManager.convertFromRupiah(remainingBalance);
            JOptionPane.showMessageDialog(frame, "Your remaining balance of " + refundedAmount + " " +
                    currencyManager.getSelectedCurrency() + " will be refunded.");
        }
    
        // Mengosongkan daftar item yang dipilih
        selectedItems.clear();
        
        // Mereset mesin (saldo dan stok barang)
        vendingMachine.reset();  // Memanggil metode reset untuk mereset saldo dan stok barang
        
        // Memperbarui tampilan daftar item yang dipilih di panel kanan
        updateSelectedItemsLabel();
    
        // Menyelesaikan transaksi atau tindakan lain yang diperlukan
        JOptionPane.showMessageDialog(frame, "Transaction Finished!");
    
        // Menampilkan kembali halaman welcome setelah transaksi selesai
        showWelcomeScreen();  // Panggil fungsi untuk kembali ke halaman welcome
    }    

    // Method untuk memperbarui saldo
    private void updateBalance() {
        if (vendingMachine == null || currencyManager == null) {
            System.err.println("Error: VendingMachine or CurrencyManager is not initialized.");
            return;
        }
        double currentBalance = vendingMachine.getBalance();
        balanceLabel.setText(String.format("Balance: %.2f %s",
                currencyManager.convertFromRupiah(currentBalance),
                currencyManager.getSelectedCurrency()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VendingMachineGUI::new);
    }
}
