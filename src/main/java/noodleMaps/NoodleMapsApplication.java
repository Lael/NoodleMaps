package noodleMaps;

import autocorrect.Trie;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.apache.commons.io.FileUtils;
import server.NoodleResource;
import server.NoodleService;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

public class NoodleMapsApplication extends Application<NoodleMapsConfiguration> {

    public static String MAPS_DIR_NAME = "maps_data";
    public static String DATABASE_FILE_NAME = "noodle_maps.data";
    public static String TILES_DIR_NAME = "tiles";
    public static String XML_DIR_NAME = "data";

    private final String jdbcDriver = "org.sqlite.JDBC";
    private final String dbUrlBase = "jdbc:sqlite:";

    public static void main(String[] args) throws Exception {
        new NoodleMapsApplication().run(args);
    }

    public void run(NoodleMapsConfiguration noodleMapsConfiguration, Environment environment) throws Exception {
        cleanDirectory();
        System.out.println("Welcome to NoodleMaps!");
        System.out.println("Verifying directory...");
        final Connection connection = verifyDirectoryAndDatabase();
        System.out.println("Starting up...");
        final NoodleService service = new NoodleService(
                connection,
                new Trie(),
                noodleMapsConfiguration.getMaxLed(),
                noodleMapsConfiguration.getNumSuggestions());
        final NoodleResource resource = new NoodleResource(service);
        environment.jersey().register(resource);
    }

    public static void fatalError(String error) {
        System.out.println("Error: " + error);
        System.exit(1);
    }

    private File checkForDirectory(File parent, String directory) {
        File dir;
        if (parent == null) {
            dir = new File(directory);
        } else {
            dir = new File(parent, directory);
        }

        if (dir.isDirectory()) {
            return dir;
        } else if (dir.isFile()) {
            fatalError("A file called \'" + directory + "\' already exists. Remove it and try again!");
        } else {
            if (!dir.mkdir()) {
                fatalError("Could not create directory \'" + directory + "\'!");
            }
        }

        return dir;
    }

    private File checkForFile(File parent, String file) {
        File f;
        if (parent == null) {
            f = new File(file);
        } else {
            f = new File(parent, file);
        }

        if (f.isFile()) {
            return f;
        } else if (f.isDirectory()) {
            fatalError("A directory called \'" + file + "\' already exists. Remove it and try again!");
        } else {
            try {
                if (!f.createNewFile()) {
                    fatalError("Could not create file \'" + file + "\'!");
                }
            } catch (IOException e) {
                fatalError("Could not create file \'" + file + "\' - " + e.getLocalizedMessage());
            }
        }

        return f;
    }

    private void createTable(Statement statement, String name, String... values) throws SQLException {
        StringBuilder sql = new StringBuilder("create table if not exists ");
        sql.append(name);
        sql.append(" (");

        for (String string : values) {
            sql.append(string);
            sql.append(", ");
        }

        sql.delete(sql.length() - 2, sql.length()); // get rid of that last comma
        sql.append(");");

        statement.executeUpdate(sql.toString());
    }

    private void createIndex(Statement statement, String indexName, String tableName, String column) {
        String sql = "create index if not exists " +
                indexName + " on " +
                tableName + "(" + column + ");";

    }

    private Connection verifyDatabase(File database) {
        // if it doesn't exist, create a database in the file
        String path = database.getAbsolutePath();
        Connection conn = null;
        try {
            Class.forName(jdbcDriver);
            conn = DriverManager.getConnection(dbUrlBase + path);
            Statement statement = conn.createStatement();

            // TODO: add defaults

            createTable(statement, "node",
                    "id bigint primary key not null",
                    "latitude double not null",
                    "longitude double not null");
            createTable(statement, "way",
                    "id bigint primary key not null",
                    "num_nodes int not null",
                    "highway_type text",
                    "street_name text",
                    "land_use text",
                    "building text",
                    "closed int default 0",
                    "one_way bool default false",
                    "length double default -1");
            createTable(statement,
                    "intersection",
                    "node_id bigint not null",
                    "way_id bigint not null",
                    "position int not null",
                    "foreign key(node_id) references node(id)",
                    "foreign key(way_id) references way(id)");
            createIndex(statement,
                    "Nind1",
                    "node",
                    "id");
            createIndex(statement,
                    "Nind2",
                    "node",
                    "latitude");
            createIndex(statement,
                    "Nind3",
                    "node",
                    "longitude");
            createIndex(statement,
                    "Wind1",
                    "way",
                    "id");
            createIndex(statement,
                    "Iind1",
                    "intersection",
                    "node_id");
            createIndex(statement,
                    "Iind2",
                    "intersection",
                    "way_id");
            System.out.println("Tables exist...");
        } catch (ClassNotFoundException e) {
            fatalError("Could not find driver class: " + e.getLocalizedMessage());
        } catch (SQLException e) {
            fatalError("Failed to create or connect to SQL database: " + e.getLocalizedMessage());
        }
        return conn;
    }

    private Connection verifyDirectoryAndDatabase() {
        // check that directory exists
        File directory = checkForDirectory(null, MAPS_DIR_NAME);

        // check that directory contains files regions.txt and noodle_maps.data, and directory tiles
        File database = checkForFile(directory, DATABASE_FILE_NAME);

        checkForDirectory(directory, TILES_DIR_NAME);
        checkForDirectory(directory, XML_DIR_NAME);

        return verifyDatabase(database);
    }

    private void cleanDirectory() {
        System.out.println("Cleaning out local data.");
        try {
            FileUtils.deleteDirectory(new File(MAPS_DIR_NAME));
        } catch (IOException e) {
            System.out.println("Failed to remove directory: " + e.getLocalizedMessage());
        }
    }
}
