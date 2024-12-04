public class CurrencyManager {
    private String selectedCurrency;

    public CurrencyManager(String currency) {
        this.selectedCurrency = currency;
    }

    public String getSelectedCurrency() {
        return selectedCurrency;
    }

    public double convertToRupiah(double amount) {
        switch (selectedCurrency) {
            case "USD":
                return amount * 15000; // Contoh konversi
            case "EUR":
                return amount * 17000; // Contoh konversi
            default: // IDR
                return amount;
        }
    }

    public double convertFromRupiah(double amount) {
        switch (selectedCurrency) {
            case "USD":
                return amount / 15000;
            case "EUR":
                return amount / 17000;
            default: // IDR
                return amount;
        }
    }
}