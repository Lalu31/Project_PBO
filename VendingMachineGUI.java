import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class VendingMachineGUI {
    private VendingMachine vendingMachine;
    private JFrame frame;
    private JLabel balanceLabel;
    private Item[] items;
    private CurrencyManager currencyManager;
    private String selectedCategory;
    private List<Item> selectedItems = new ArrayList<>(); // Menyimpan item yang dipilih
    private JLabel itemNameLabel; // Deklarasi label untuk menampilkan nama item yang dipilih

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
        frame.setSize(700, 500);

        startBackgroundMusic();

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
        String query = "SELECT imagePath FROM images WHERE imageName = 'WelcomeImage'"; // Ambil gambar pertama,
                                                                                        // sesuaikan query jika perlu

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
        leftPanel.setBackground(Color.WHITE);

        // Ambil path gambar dari database
        String imagePath = getImagePathFromDatabase();
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = ""; // Gambar default
        }

        // Membuat ImageIcon dengan path gambar
        ImageIcon imageIcon = new ImageIcon(imagePath);

        // Membuat JLabel dengan ImageIcon
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Simulasi gambar dengan border

        // Panel Kanan (untuk teks, tombol "GO", tombol "History Pembelian", dan jam)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(new Color(214, 207, 194));

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

        // Label untuk jam
        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.ITALIC, 20));
        timeLabel.setForeground(Color.BLACK);

        // Timer untuk memperbarui jam
        Timer timer = new Timer(1000, e -> {
            String currentTime = new SimpleDateFormat("HH:mm").format(new Date());
            timeLabel.setText("Time: " + currentTime);
        });
        timer.start();

        // Tombol "GO" di sebelah kanan
        JButton startButton = new JButton("GO");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setFont(new Font("Arial", Font.ITALIC, 12));
        startButton.setBackground(new Color(183, 155, 113));
        startButton.setOpaque(true);
        startButton.setBorderPainted(true);
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> showCategorySelection());
        startButton.setPreferredSize(new Dimension(150, 30));
        startButton.setMaximumSize(new Dimension(150, 30));

        // Tombol "History Pembelian" di bawah tombol "GO"
        JButton historyButton = new JButton("Purchase History");
        historyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        historyButton.setFont(new Font("Arial", Font.ITALIC, 12));
        historyButton.setBackground(new Color(183, 155, 113));
        historyButton.setOpaque(true);
        historyButton.setBorderPainted(true);
        historyButton.setFocusPainted(false);
        historyButton.addActionListener(e -> showPurchaseHistory());
        historyButton.setPreferredSize(new Dimension(150, 30));
        historyButton.setMaximumSize(new Dimension(150, 30));

        // Tombol "Game" untuk menampilkan permainan
        JButton gameButton = new JButton("Block Puzzle Game");
        gameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameButton.setFont(new Font("Arial", Font.ITALIC, 12));
        gameButton.setBackground(new Color(183, 155, 113));
        gameButton.setOpaque(true);
        gameButton.setBorderPainted(true);
        gameButton.setFocusPainted(false);
        gameButton.addActionListener(e -> showGameScreen()); // Menampilkan game saat tombol ditekan
        gameButton.setPreferredSize(new Dimension(150, 30));
        gameButton.setMaximumSize(new Dimension(150, 30));

        // Menambahkan elemen ke panel kiri (gambar)
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(imageLabel);
        leftPanel.add(Box.createVerticalGlue());

        // Menambahkan elemen ke panel kanan (teks, tombol, dan jam)
        JPanel topRightPanel = new JPanel();
        topRightPanel.setLayout(new BoxLayout(topRightPanel, BoxLayout.Y_AXIS));
        topRightPanel.setBackground(new Color(214, 207, 194));

        topRightPanel.add(Box.createVerticalGlue());
        topRightPanel.add(titleLabel);
        topRightPanel.add(Box.createVerticalStrut(20));
        topRightPanel.add(subtitleLabel);
        topRightPanel.add(Box.createVerticalStrut(20));
        topRightPanel.add(startButton);
        topRightPanel.add(Box.createVerticalStrut(10));
        topRightPanel.add(historyButton); // Tombol "History Pembelian"
        topRightPanel.add(Box.createVerticalStrut(10));
        topRightPanel.add(gameButton); // Tombol "Game"
        topRightPanel.add(Box.createVerticalGlue());

        // Menambahkan panel atas (teks dan tombol) ke panel kanan
        rightPanel.add(topRightPanel, BorderLayout.CENTER);

        // Menambahkan label jam di bagian bawah kanan
        JPanel bottomRightPanel = new JPanel(new BorderLayout());
        bottomRightPanel.setBackground(new Color(214, 207, 194));
        bottomRightPanel.add(timeLabel, BorderLayout.WEST); // Posisi kiri bawah

        // Menambahkan panel bawah (jam) ke panel kanan
        rightPanel.add(bottomRightPanel, BorderLayout.SOUTH);

        // Menambahkan panel kiri dan kanan ke panel utama
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        // Menampilkan panel utama
        setContent(mainPanel);
        frame.setVisible(true);
    }

    private void showGameScreen() {
        JFrame gameFrame = new JFrame("Block Puzzle Game");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(800, 435);
        gameFrame.setResizable(false);

        JPanel gamePanel = new JPanel(new BorderLayout());

        // Panel kiri untuk game
        BlockPuzzleGame blockPuzzleGame = new BlockPuzzleGame();
        gamePanel.add(blockPuzzleGame, BorderLayout.WEST);

        // Panel kanan untuk tombol "Back"
        JPanel gameRightPanel = new JPanel();
        gameRightPanel.setLayout(new BoxLayout(gameRightPanel, BoxLayout.Y_AXIS));
        gameRightPanel.setBackground(new Color(214, 207, 194));

        // Tombol back
        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setFont(new Font("Arial", Font.ITALIC, 12));
        backButton.setBackground(new Color(183, 155, 113));
        backButton.setOpaque(true);
        backButton.setBorderPainted(true);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            blockPuzzleGame.stopGame(); // Hentikan game
            showWelcomeScreen(); // Kembali ke tampilan welcome
            gameFrame.dispose(); // Tutup game frame
        });
        backButton.setPreferredSize(new Dimension(150, 30));
        backButton.setMaximumSize(new Dimension(150, 30));

        gameRightPanel.add(Box.createVerticalGlue());
        gameRightPanel.add(backButton);
        gameRightPanel.add(Box.createVerticalGlue());

        gamePanel.add(gameRightPanel, BorderLayout.CENTER);
        gameFrame.setContentPane(gamePanel);
        gameFrame.setVisible(true);
    }

    private void showPurchaseHistory() {
        // Panel utama dengan BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Panel kiri (informasi riwayat pembelian)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(240, 240, 240));

        // Label judul
        JLabel titleLabel = new JLabel("Purchase History", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.ITALIC, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabel untuk menampilkan data riwayat pembelian
        String[] columnNames = { "Item Name", "Item Price", "Purchase Date" };
        Object[][] data = fetchPurchaseHistory();
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        table.setBackground(new Color(240, 240, 240));
        scrollPane.getViewport().setBackground(new Color(240, 240, 240));
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel kanan (untuk tombol Back)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(214, 207, 194));
        rightPanel.setPreferredSize(new Dimension(300, 0)); // Lebarkan panel kanan

        // Tombol "Back"
        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setFont(new Font("Arial", Font.ITALIC, 12));
        backButton.setBackground(new Color(183, 155, 113));
        backButton.setOpaque(true);
        backButton.setBorderPainted(true);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> showWelcomeScreen());
        backButton.setPreferredSize(new Dimension(100, 30));

        // Tombol Delete
        JButton deleteButton = new JButton("Delete");
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton.setFont(new Font("Arial", Font.ITALIC, 12));
        deleteButton.setBackground(new Color(183, 155, 113));
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(true);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deletePurchaseHistory());
        deleteButton.setPreferredSize(new Dimension(150, 30));

        // Tambahkan tombol ini ke panel GUI
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(deleteButton);
        rightPanel.add(Box.createVerticalStrut(20));
  
        // Menambahkan tombol ke panel kanan
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(backButton);
        rightPanel.add(Box.createVerticalGlue());

        // Menambahkan panel kiri dan kanan ke panel utama
        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        // Menampilkan panel utama
        setContent(mainPanel);
        frame.setVisible(true);
    }

    // Fungsi untuk menghapus riwayat pembelian
    private void deletePurchaseHistory() {
        String[] options = {"Delete Selected", "Delete All", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "What would you like to do?",
                "Delete Purchase History",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]
        );

        switch (choice) {
            case 0: // Delete Selected
                String selectedItem = JOptionPane.showInputDialog(null, "Enter the item name to delete:", "Delete Selected", JOptionPane.QUESTION_MESSAGE);
                if (selectedItem != null && !selectedItem.trim().isEmpty()) {
                    deleteSinglePurchase(selectedItem);
                }
                break;

            case 1: // Delete All
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete all purchase history?", "Delete All", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteAllPurchases();
                }
                break;

            case 2: // Cancel
            default:
                // Do nothing
                break;
        }
    }

    // Fungsi untuk menghapus satu entri pembelian
    private void deleteSinglePurchase(String itemName) {
        String query = "DELETE FROM purchase_history WHERE item_name = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, itemName);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Purchase deleted successfully: " + itemName, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No purchase found with the name: " + itemName, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting purchase: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Fungsi untuk menghapus semua entri pembelian
    private void deleteAllPurchases() {
        String query = "DELETE FROM purchase_history";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query)) {

            int rowsAffected = stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "All purchase history deleted successfully. " + rowsAffected, "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting all purchases: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Object[][] fetchPurchaseHistory() {
        String query = "SELECT item_name, item_price, purchase_date FROM purchase_history"; // Menghapus kolom "id" dari
                                                                                            // query
        List<Object[]> historyList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String itemName = rs.getString("item_name");
                double itemPrice = rs.getDouble("item_price");
                Timestamp purchaseDate = rs.getTimestamp("purchase_date");

                // Menambahkan data ke list tanpa ID
                historyList.add(new Object[] { itemName, itemPrice, purchaseDate });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Konversi list ke array 2D untuk JTable
        return historyList.toArray(new Object[0][0]);
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
        rightPanel.setLayout(new BorderLayout()); // Menggunakan BorderLayout untuk penempatan waktu di bawah kiri
        rightPanel.setBackground(new Color(214, 207, 194));

        // Label untuk kategori
        JLabel categoryLabel = new JLabel("Select a category:");
        categoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        categoryLabel.setFont(new Font("Arial", Font.ITALIC, 24));
        categoryLabel.setForeground(Color.BLACK);

        // Tombol kategori "Snacks"
        JButton snackButton = new JButton("Snacks");
        styleButton2(snackButton, new Color(183, 155, 113)); // Warna Coklat Tua
        snackButton.addActionListener(e -> {
            selectedCategory = "Snacks";
            showCurrencySelection();
        });

        // Tombol kategori "Drinks"
        JButton drinkButton = new JButton("Drinks");
        styleButton2(drinkButton, new Color(183, 155, 113));
        drinkButton.addActionListener(e -> {
            selectedCategory = "Drinks";
            showCurrencySelection();
        });

        // Tombol "Back"
        JButton backButton = new JButton("Back");
        styleButton2(backButton, new Color(183, 155, 113));
        backButton.addActionListener(e -> showWelcomeScreen());

        // Menambahkan elemen ke panel kanan
        JPanel topRightPanel = new JPanel();
        topRightPanel.setLayout(new BoxLayout(topRightPanel, BoxLayout.Y_AXIS));
        topRightPanel.setBackground(new Color(214, 207, 194));

        topRightPanel.add(Box.createVerticalGlue());
        topRightPanel.add(categoryLabel);
        topRightPanel.add(Box.createVerticalStrut(20)); // Jarak antara label dan tombol pertama
        topRightPanel.add(snackButton);
        topRightPanel.add(Box.createVerticalStrut(20)); // Jarak antara tombol-tombol
        topRightPanel.add(drinkButton);
        topRightPanel.add(Box.createVerticalStrut(20));
        topRightPanel.add(backButton);
        topRightPanel.add(Box.createVerticalGlue());

        // Menambahkan panel atas (teks dan tombol) ke panel kanan
        rightPanel.add(topRightPanel, BorderLayout.CENTER);

        // Menambahkan label jam di bagian bawah kiri
        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        timeLabel.setForeground(Color.BLACK);

        // Timer untuk memperbarui jam
        Timer timer = new Timer(1000, e -> {
            String currentTime = new SimpleDateFormat("HH:mm").format(new Date());
            timeLabel.setText("Time: " + currentTime);
        });
        timer.start();

        // Panel untuk waktu
        JPanel bottomRightPanel = new JPanel(new BorderLayout());
        bottomRightPanel.setBackground(new Color(214, 207, 194));
        bottomRightPanel.add(timeLabel, BorderLayout.WEST); // Posisi kiri bawah

        // Menambahkan panel bawah (jam) ke panel kanan
        rightPanel.add(bottomRightPanel, BorderLayout.SOUTH);

        // Menambahkan panel kiri dan kanan ke panel utama
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        // Menampilkan panel utama
        setContent(mainPanel);
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
        rightPanel.setLayout(new BorderLayout()); // Menggunakan BorderLayout untuk penempatan waktu di bawah kiri
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
        styleButton2(backButton, new Color(183, 155, 113)); // Coklat tua
        backButton.addActionListener(e -> showCategorySelection()); // Kembali ke kategori

        // Menambahkan elemen ke panel kanan
        JPanel topRightPanel = new JPanel();
        topRightPanel.setLayout(new BoxLayout(topRightPanel, BoxLayout.Y_AXIS));
        topRightPanel.setBackground(new Color(214, 207, 194));

        topRightPanel.add(Box.createVerticalGlue());
        topRightPanel.add(currencyLabel);
        topRightPanel.add(Box.createVerticalStrut(20)); // Jarak antara label dan tombol pertama
        topRightPanel.add(idrButton);
        topRightPanel.add(Box.createVerticalStrut(20)); // Jarak antara tombol-tombol
        topRightPanel.add(usdButton);
        topRightPanel.add(Box.createVerticalStrut(20));
        topRightPanel.add(eurButton);
        topRightPanel.add(Box.createVerticalStrut(20));
        topRightPanel.add(backButton);
        topRightPanel.add(Box.createVerticalGlue());

        // Menambahkan panel atas (teks dan tombol) ke panel kanan
        rightPanel.add(topRightPanel, BorderLayout.CENTER);

        // Menambahkan label jam di bagian bawah kiri
        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        timeLabel.setForeground(Color.BLACK);

        // Timer untuk memperbarui jam
        Timer timer = new Timer(1000, e -> {
            String currentTime = new SimpleDateFormat("HH:mm").format(new Date());
            timeLabel.setText("Time: " + currentTime);
        });
        timer.start();

        // Panel untuk waktu
        JPanel bottomRightPanel = new JPanel(new BorderLayout());
        bottomRightPanel.setBackground(new Color(214, 207, 194));
        bottomRightPanel.add(timeLabel, BorderLayout.WEST); // Posisi kiri bawah

        // Menambahkan panel bawah (jam) ke panel kanan
        rightPanel.add(bottomRightPanel, BorderLayout.SOUTH);

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
        button.setMaximumSize(new Dimension(150, 40)); // Ukuran maksimum tombol
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

                // Membuat tombol dengan nama item dan harga
                JButton itemButton = new JButton(String.format("%s - %.2f %s (%d)", item.getName(),
                        currencyManager.convertFromRupiah(item.getPrice()),
                        currencyManager.getSelectedCurrency(),
                        item.getStock()));

                // Menentukan path gambar berdasarkan kategori item
                String imagePath = "";
                if (item instanceof Snack) {
                    imagePath = "assets/makanan/" + item.getName().toLowerCase().replace(" ", "") + ".png";
                } else if (item instanceof Drink) {
                    imagePath = "assets/minuman/" + item.getName().toLowerCase().replace(" ", "") + ".png";
                }

                // Memuat gambar jika path valid
                ImageIcon icon = new ImageIcon(imagePath);
                Image image = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH); // Menyesuaikan ukuran
                                                                                             // gambar
                itemButton.setIcon(new ImageIcon(image)); // Set gambar pada tombol

                itemButton.setHorizontalTextPosition(SwingConstants.CENTER); // Posisi teks di bawah gambar
                itemButton.setVerticalTextPosition(SwingConstants.BOTTOM); // Posisi teks di bawah gambar

                // Mengatur warna awal tombol menjadi abu-abu
                itemButton.setBackground(Color.lightGray);

                // Menandai tombol dengan warna hijau jika item dipilih
                if (selectedItems.contains(item)) {
                    itemButton.setBackground(Color.GREEN);
                }

                itemButton.setFont(new Font("Arial", Font.ITALIC, 16)); // Menetapkan font

                itemButton.addActionListener(e -> {
                    // Memilih item dan menambahkannya ke daftar item yang dipilih
                    if (!selectedItems.contains(item)) {
                        selectedItems.add(item);
                        itemButton.setBackground(Color.GREEN); // Menandai item yang dipilih
                    } else {
                        selectedItems.remove(item);
                        itemButton.setBackground(Color.lightGray);
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
        rightPanel.setBackground(new Color(214, 207, 194)); // Panel kanan dengan warna latar belakang RGB(214, 207,
                                                            // 194)
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
        leftPanel.setPreferredSize(new Dimension(1100, mainPanel.getHeight())); // Lebar panel kiri yang sesuai

        // Panel kanan dengan lebar tetap dan memberikan sedikit margin agar lebih
        // seimbang
        rightPanel.setPreferredSize(new Dimension(350, mainPanel.getHeight())); // Panel kanan lebih lebar dari
                                                                                // sebelumnya

        // Menambahkan panel kiri dan kanan ke panel utama
        mainPanel.add(leftPanel, BorderLayout.WEST); // Panel kiri untuk tombol item
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
            button.setBackground(Color.LIGHT_GRAY); // Set background color to light gray

            // ActionListener to append the number to the input field
            button.addActionListener(e -> {
                button.setBackground(Color.GREEN); // Change color to green on button press
                amountField.setText(amountField.getText() + button.getText());

                // Optionally, add a delay to revert the color after a short time
                Timer timer = new Timer(100, event -> button.setBackground(Color.LIGHT_GRAY)); // Revert color after
                                                                                               // 100ms
                timer.setRepeats(false);
                timer.start();
            });

            inputPanel.add(button);
        }

        // Tombol Add di kolom pertama pada baris terakhir
        addButton.setBackground(Color.LIGHT_GRAY); // Set background color to light gray
        addButton.addActionListener(e -> {
            addButton.setBackground(Color.GREEN); // Change color to green on press
            amountField.setText(amountField.getText()); // Logic for addButton can be here

            // Revert color after 100ms
            Timer timer = new Timer(100, event -> addButton.setBackground(Color.LIGHT_GRAY));
            timer.setRepeats(false);
            timer.start();
        });
        inputPanel.add(addButton);

        // Tombol 0 di kolom kedua pada baris terakhir
        JButton zeroButton = new JButton("0");
        zeroButton.setBackground(Color.LIGHT_GRAY); // Set background color to light gray
        zeroButton.addActionListener(e -> {
            zeroButton.setBackground(Color.GREEN); // Change color to green on press
            amountField.setText(amountField.getText() + "0");

            // Revert color after 100ms
            Timer timer = new Timer(100, event -> zeroButton.setBackground(Color.LIGHT_GRAY));
            timer.setRepeats(false);
            timer.start();
        });
        inputPanel.add(zeroButton);

        // Tombol delete (<--) di kolom ketiga pada baris terakhir
        deleteButton.setBackground(Color.LIGHT_GRAY); // Set background color to light gray
        deleteButton.addActionListener(e -> {
            deleteButton.setBackground(Color.GREEN); // Change color to green on press
            String text = amountField.getText();
            if (!text.isEmpty()) {
                amountField.setText(text.substring(0, text.length() - 1)); // Delete last character
            }

            // Revert color after 100ms
            Timer timer = new Timer(100, event -> deleteButton.setBackground(Color.LIGHT_GRAY));
            timer.setRepeats(false);
            timer.start();
        });
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

        // **Add Rupiah Label** for total price in Rupiah
        JLabel rupiahLabel = new JLabel("Rupiah: 0.00", SwingConstants.CENTER);
        rupiahLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        rupiahLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Update the Rupiah label when the total price changes
        updateRupiahLabel(rupiahLabel);

        // Membuat tombol dengan ukuran seragam
        Dimension buttonSize = new Dimension(120, 30);

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setPreferredSize(buttonSize);
        backButton.setMaximumSize(buttonSize); // Menambahkan untuk memastikan ukuran tombol tetap seragam
        backButton.setFont(new Font("Arial", Font.ITALIC, 18));
        backButton.setBackground(new Color(183, 155, 113));
        backButton.setOpaque(true);
        backButton.setBorderPainted(true);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> showItemSelection());

        JButton buyButton = new JButton("Buy");
        buyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buyButton.setPreferredSize(buttonSize);
        buyButton.setMaximumSize(buttonSize); // Menambahkan untuk memastikan ukuran tombol tetap seragam
        buyButton.setFont(new Font("Arial", Font.ITALIC, 18));
        buyButton.setBackground(new Color(183, 155, 113));
        buyButton.setOpaque(true);
        buyButton.setBorderPainted(true);
        buyButton.setFocusPainted(false);
        buyButton.addActionListener(e -> startDispensingProcess(selectedItems));

        JButton finishButton = new JButton("Finish");
        finishButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        finishButton.setPreferredSize(buttonSize);
        finishButton.setMaximumSize(buttonSize); // Menambahkan untuk memastikan ukuran tombol tetap seragam
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
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(rupiahLabel); // **Add the Rupiah label here**
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

    // Update Rupiah Label function
    private void updateRupiahLabel(JLabel rupiahLabel) {
        double totalPriceInRupiah = 0;
        for (Item item : selectedItems) {
            totalPriceInRupiah += currencyManager.convertToRupiah(item.getPrice());
        }
        rupiahLabel.setText("Rupiah: " + currencyManager.convertFromRupiah(totalPriceInRupiah));
    }

    // Fungsi untuk memperbarui total harga
    private void updateTotalPriceLabel(JLabel totalPriceLabel) {
        double totalPrice = 0;
        for (Item item : selectedItems) {
            totalPrice += item.getPrice();
        }
        totalPriceLabel.setText("Total Price: " + currencyManager.convertFromRupiah(totalPrice) + " "
                + currencyManager.getSelectedCurrency());
    }

    private String getSoundPathByItemType(String itemType) {
        String soundPath = null;

        // SQL query untuk mengambil soundPath berdasarkan itemType
        String query = "SELECT soundPath FROM sound WHERE itemType = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {

            // Set parameter itemType (kategori item)
            stmt.setString(1, itemType);

            // Eksekusi query
            ResultSet rs = stmt.executeQuery();

            // Jika hasil ditemukan, ambil soundPath
            if (rs.next()) {
                soundPath = rs.getString("soundPath");
                System.out.println("Path suara yang ditemukan: " + soundPath); // Debugging
            } else {
                System.out.println("Path suara tidak ditemukan untuk itemType: " + itemType); // Debugging
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return soundPath; // Mengembalikan path suara atau null jika tidak ditemukan
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

        // Hentikan musik saat proses loading dimulai
        stopBackgroundMusic();

        // Simulasi proses dispensing item
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
                    for (Item item : selectedItems) {
                        String itemType = item instanceof Snack ? "Snack" : "Drink"; // Tentukan kategori item

                        // Ambil path suara berdasarkan kategori item
                        String soundPath = getSoundPathByItemType(itemType);

                        if (soundPath != null) {
                            // Memutar suara untuk item kategori yang dipilih
                            new Thread(() -> {
                                SoundManager.playSound(soundPath);
                            }).start();
                        } else {
                            System.out.println("Path suara tidak ditemukan untuk item: " + item.getName());
                        }

                        // Simulasi proses dispensing item
                        vendingMachine.dispenseItem(item); // Dispense each selected item
                        Thread.sleep(item instanceof Drink ? 6000 : 1000); // Durasi berdasarkan item
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

                // Start background music again after dispensing is done
                startBackgroundMusic(); // Melanjutkan musik latar setelah loading selesai
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

        // Menampilkan sisa saldo jika ada
        if (remainingBalance > 0) {
            // Refund remaining balance to the selected currency
            double refundedAmount = currencyManager.convertFromRupiah(remainingBalance);
            JOptionPane.showMessageDialog(frame, "Your remaining balance of " + refundedAmount + " " +
                    currencyManager.getSelectedCurrency() + " will be refunded.");
        }

        // Simpan pembelian ke database
        for (Item item : selectedItems) {
            savePurchaseToDatabase(item.getName(), item.getPrice());
        }

        // Mengosongkan daftar item yang dipilih
        selectedItems.clear();

        // Mereset mesin (saldo dan stok barang)
        vendingMachine.reset(); // Memanggil metode reset untuk mereset saldo dan stok barang

        // Memperbarui tampilan daftar item yang dipilih di panel kanan
        updateSelectedItemsLabel();

        // Menyelesaikan transaksi atau tindakan lain yang diperlukan
        JOptionPane.showMessageDialog(frame, "Transaction Finished!");

        // Menampilkan kembali halaman welcome setelah transaksi selesai
        showWelcomeScreen(); // Panggil fungsi untuk kembali ke halaman welcome
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

    // Method to get background music path from the database
    private String getBackgroundMusicPath() {
        String musicPath = null;
        String query = "SELECT soundPath FROM sound WHERE itemType = 'Background' LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {
            // Execute query
            ResultSet rs = stmt.executeQuery();

            // If result is found, get soundPath
            if (rs.next()) {
                musicPath = rs.getString("soundPath");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return musicPath; // Return background music path or null if not found
    }

    private long musicPosition = 0; // Menyimpan posisi musik saat dihentikan
    private Clip backgroundClip; // Objek Clip untuk musik latar

    // Method untuk memutar background music
    private void playBackgroundMusic(String musicPath) {
        try {
            // Jika backgroundClip sudah ada dan sedang diputar, simpan posisinya
            if (backgroundClip != null && backgroundClip.isRunning()) {
                musicPosition = backgroundClip.getMicrosecondPosition();
                backgroundClip.stop(); // Hentikan musik
            }

            // Membuka file musik dan memulai pemutaran
            File musicFile = new File(musicPath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioStream);

            // Jika ada posisi yang disimpan, lanjutkan dari posisi itu
            if (musicPosition > 0) {
                backgroundClip.setMicrosecondPosition(musicPosition);
            }

            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY); // Memutar musik dalam loop
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Method untuk menghentikan background music
    private void stopBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            musicPosition = backgroundClip.getMicrosecondPosition(); // Menyimpan posisi saat ini
            backgroundClip.stop(); // Menghentikan musik latar
        }
    }

    // Memulai ulang musik latar setelah loading selesai
    private void startBackgroundMusic() {
        String musicPath = getBackgroundMusicPath(); // Mendapatkan path musik dari database
        if (musicPath != null) {
            playBackgroundMusic(musicPath); // Memulai musik latar atau melanjutkan dari posisi terakhir
        } else {
            System.out.println("Background music not found.");
        }
    }

    private void savePurchaseToDatabase(String itemName, double itemPrice) {
        String query = "INSERT INTO purchase_history (item_name, item_price) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query)) {

            // Set parameter untuk query
            stmt.setString(1, itemName);
            stmt.setDouble(2, itemPrice);

            // Eksekusi query
            stmt.executeUpdate();
            System.out.println("Purchase saved to database: " + itemName + " - " + itemPrice);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VendingMachineGUI::new);
    }
}