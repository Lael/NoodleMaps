package healthChecks;

import com.codahale.metrics.health.HealthCheck;

import java.sql.Connection;

import static noodleMaps.NoodleMapsApplication.DATABASE_FILE_NAME;

public class DatabaseHealthCheck extends HealthCheck {
    private final Connection connection;

    public DatabaseHealthCheck(Connection connection) {
        this.connection = connection;
    }

    @Override
    protected Result check() throws Exception {
        if (connection.isValid(0)){
            return Result.healthy();
        } else {
            return Result.unhealthy("Cannot connect to " + DATABASE_FILE_NAME);
        }
    }
}