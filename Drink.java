public class Drink {
    private String id;            
    private String namaDrink;    
    private String imgDrink;    
    private double price;     
    private int quantity;         

    /* Konstruktor */ 
    public Drink(String id, String namaDrink, String imgDrink, double price, int quantity) {
        this.id = id;
        this.namaDrink = namaDrink;
        this.imgDrink = imgDrink;
        this.price = price;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamaDrink() {
        return namaDrink;
    }

    public void setNamaDrink(String namaDrink) {
        this.namaDrink = namaDrink;
    }

    public String getImageDrink() {
        return imgDrink;
    }

    public void setImageDrink(String imgDrink) {
        this.imgDrink = imgDrink;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Metode untuk mengurangi stok
    public void decreaseQuantity() {
        if (quantity > 0) {
            quantity--;
        }
    }

    // Metode untuk menambah stok
    public void increaseQuantity() {
        quantity++;
    }
}