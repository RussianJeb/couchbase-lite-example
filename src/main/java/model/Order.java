package model;

/**
 * @author Jeb
 */
public class Order {
    private final String store;
    private final String product;
    private final int quantity;

    public Order(String store, String product, int quantity) {
        this.store = store;
        this.product = product;
        this.quantity = quantity;
    }

    public String getStore() {
        return store;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "Order{" +
                "store='" + store + '\'' +
                ", product='" + product + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
