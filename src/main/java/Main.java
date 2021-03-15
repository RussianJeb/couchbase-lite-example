import com.couchbase.lite.*;
import model.Order;
import repository.OrderRepository;
import service.SynchronizationService;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * @author Jeb
 */
public class Main {

    private static final String DB = "database_test";
    private static final String STORE = "Costco";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";
    private static final String URL = "ws://localhost:4984/db";

    private static final String[] PRODUCTS = {
            "Orange",
            "Apple",
            "Avocado",
            "Banana",
            "Cucumber"
    };

    public static void main(String[] args) throws CouchbaseLiteException, URISyntaxException, InterruptedException, ExecutionException {
        Path dbPath = Paths.get(DB);
        String dbName = "db";
        Random random = new Random();
        CouchbaseLite.init();
        DatabaseConfiguration config = new DatabaseConfiguration();
        config.setDirectory(dbPath.toString());
        Database database = new Database(dbName, config);
        OrderRepository orderRepository = new OrderRepository(database);
        Authenticator authenticator = new BasicAuthenticator(USERNAME, PASSWORD.toCharArray());
        SynchronizationService synchronizationService = new SynchronizationService(
                database,
                URL,
                authenticator
        );
        for (int i = 0; i < 20; i++) {
            Order order = new Order(
                    STORE,
                    PRODUCTS[random.nextInt(PRODUCTS.length)],
                    random.nextInt(100)
            );
            orderRepository.save(order);
        }
        synchronizationService.sync().get();
        orderRepository.findAll().forEach(System.out::println);
        System.exit(0);
    }
}
