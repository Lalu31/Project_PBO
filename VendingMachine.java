public class VendingMachine {
    private double balance; // Saldo dalam Rupiah
    private Item[] items; // Daftar barang (Drink dan Snack)

    public VendingMachine() {
        balance = 0; // Inisialisasi saldo ke 0
        // Menambahkan semua jenis barang (Drink dan Snack)
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
    }

    // Fungsi untuk menambahkan saldo ke mesin
    public void addBalance(double amount) {
        balance += amount;
    }

    // Fungsi untuk mereset mesin (saldo dan stok barang)
    public void reset() {
        balance = 0; // Reset saldo ke 0
        // Reset stok setiap item jika diperlukan
        for (Item item : items) {
            item.resetStock(); // Reset stok setiap item
        }
    }

    // Fungsi untuk mendapatkan saldo mesin dalam Rupiah
    public double getBalance() {
        return balance;
    }

    // Fungsi untuk mendispense item (mengurangi stok)
    public boolean dispenseItem(Item item) {
        if (item.getStock() > 0 && balance >= item.getPrice()) {
            balance -= item.getPrice(); // Kurangi saldo
            item.decreaseStock(); // Kurangi stok
            return true; // Item berhasil dispensed
        }
        return false; // Gagal jika saldo tidak cukup atau stok habis
    }

    // Fungsi untuk mendapatkan daftar barang
    public Item[] getItems() {
        return items;
    }

    // Fungsi untuk mencari item berdasarkan nama
    public Item getItemByName(String name) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null; // Jika item tidak ditemukan
    }
}