package service;

import com.couchbase.lite.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Jeb
 */
public class SynchronizationService {
    private final ExecutorService synchronizationExecutor = Executors.newSingleThreadExecutor();

    private final Database database;
    private final Endpoint endpoint;
    private final Authenticator authenticator;

    public SynchronizationService(Database database,
                                  String wsUrl,
                                  Authenticator authenticator) throws URISyntaxException {
        this.database = database;
        this.endpoint = new URLEndpoint(new URI(wsUrl));
        this.authenticator = authenticator;
    }


    public Future<?> push() {
        var replicator = createReplicator(ReplicatorConfiguration.ReplicatorType.PUSH);
        return synchronizationExecutor.submit(() -> sync(replicator));
    }

    public Future<?> pull() {
        var replicator = createReplicator(ReplicatorConfiguration.ReplicatorType.PULL);
        return synchronizationExecutor.submit(() -> sync(replicator));
    }

    public Future<?> sync() {
        var replicator = createReplicator(ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL);
        return synchronizationExecutor.submit(() -> sync(replicator));
    }

    private void sync(Replicator replicator) {
        replicator.addChangeListener(change -> {
            if (change.getStatus().getError() != null) {
                System.err.println("Error code ::  " + change.getStatus().getError().getCode());
            }
        });

        replicator.start(true);

        while (replicator.getStatus().getActivityLevel() != Replicator.ActivityLevel.STOPPED) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Replicator createReplicator(ReplicatorConfiguration.ReplicatorType type) {
        ReplicatorConfiguration replConfig = new ReplicatorConfiguration(database, endpoint);
        replConfig.setReplicatorType(type);
        replConfig.setAuthenticator(authenticator);
        return new Replicator(replConfig);
    }


}
