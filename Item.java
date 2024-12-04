public abstract class Item {
    private String name;
    private double price;
    private int stock;

    public Item(String name, double price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void decreaseStock() {
        if (stock > 0) {
            stock--;
        }
    }

    // Reset stok item (bisa digunakan saat reset mesin)
    public void resetStock() {
        // Misalnya, set stok kembali ke nilai awal jika diperlukan
        this.stock = 10; // Misalnya reset ke stok 10
    }
}

class Drink extends Item {
    public Drink(String name, double price, int stock) {
        super(name, price, stock);
    }
}

class Snack extends Item {
    public Snack(String name, double price, int stock) {
        super(name, price, stock);
    }
}
