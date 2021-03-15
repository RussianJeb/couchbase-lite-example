package repository;

import com.couchbase.lite.*;
import model.Order;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Jeb
 */
public class OrderRepository {
    private static final String PROPERTY_STORE = "store";
    private static final String PROPERTY_PRODUCT = "product";
    private static final String PROPERTY_QUANTITY = "quantity";

    private final Database database;

    public OrderRepository(Database database) {
        this.database = database;
    }


    public void save(Order order) {
        MutableDocument document = new MutableDocument()
                .setString(PROPERTY_STORE, order.getStore())
                .setString(PROPERTY_PRODUCT, order.getProduct())
                .setInt(PROPERTY_QUANTITY, order.getQuantity());
        try {
            database.save(document);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public Collection<Order> findAll() {
        var query = QueryBuilder.select(
                SelectResult.property(PROPERTY_STORE),
                SelectResult.property(PROPERTY_QUANTITY),
                SelectResult.property(PROPERTY_PRODUCT)
        )
                .from(DataSource.database(database));
        var orders = new ArrayList<Order>();
        try {
            for (var result : query.execute()) {
                var order = new Order(
                        result.getString(PROPERTY_STORE),
                        result.getString(PROPERTY_PRODUCT),
                        result.getInt(PROPERTY_QUANTITY)
                );
                orders.add(order);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return orders;
    }
}
