package noodleMaps;

import autocorrect.Trie;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class NoodleMapsApplication {

    public static String MAPS_DIR_NAME = "maps_data";
    public static String REGIONS_FILE_NAME = "regions.txt";
    public static String DATABASE_FILE_NAME = "noodle_maps.data";
    public static String TILES_DIR_NAME = "tiles";
    public static String XML_DIR_NAME = "data";

    private final String jdbcDriver = "org.sqlite.JDBC";
    private final String dbUrlBase = "jdbc:sqlite:";

    private double startLat = 41.75;
    private double startLon = -71.45;


    public static void main(String[] args) {
        NoodleMapsApplication main = new NoodleMapsApplication();
        main.run(args);
    }

    private void run(String[] args) {
        OptionSet options = checkArgs(args);

        /* -h, -C, */
        if (options.has("h")) {
            helpMessage();
        }

        if (options.has("C")) {
            cleanDirectory();
            System.out.println("Bye!");
            System.exit(0);
        }

        if (options.has("c")) {
            cleanDirectory();
        }

        if (options.has("t")) {
            startLat = (Double) options.valueOf("t");
            if (options.has("n")) {
                startLon = (Double) options.valueOf("n");
            } else {
                fatalError("The options -t and -n must be used together or not at all.");
            }
        } else {
            if (options.has("n")) {
                fatalError("The options -t and -n must be used together or not at all.");
            }
        }

        System.out.println("Welcome to NoodleMaps!");
        System.out.println("Verifying directory...");
        Connection connection = verifyDirectoryAndDatabase();

        System.out.println("Starting up...");
        if (options.has("g")) {
            NoodleServer server = new NoodleServer(startLat, startLon, options.has("a"));
        } else {
            NoodleRepl repl = new NoodleRepl(startLat, startLon);
        }

//        Date start = new Date();

//        System.out.println(start.toString());
//
//        try {
//            FileEater fileEater = new FileEater(connection);
//            fileEater.consumeXml(new FileInputStream("map_data/map_data-71.450,41.750,-71.440,41.760.xml"));
//        } catch (Exception e) {
//            e.printStackTrace();
//            fatalError(e.getLocalizedMessage());
//        }
//        Date end = new Date();
//        System.out.println(end.toString());
        Trie trie = new Trie();
        try {
            Scanner scanner = new Scanner(new File("/Users/laelcosta/Desktop/dictionary.txt"));
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine();
                trie.add(word);
            }
            System.out.println("--- abc ---");
            long start_time = System.nanoTime();
            List<String> words = trie.wordsWithinDistance("abc", 0);
            long end_time = System.nanoTime();
            double difference = (end_time - start_time)/1e9;
            for (String s : words) {
                System.out.println(s);
            }
            System.out.println("--- --- ---");
            System.out.println(difference + "s");
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            connection.close();
        } catch (Exception e) {
            fatalError(e.getLocalizedMessage());
        }
    }


    private void usageError() {
        System.out.println("Usage: ./noodleMaps [-acCgh] [-t <lat> -n <lon>] [-s <size>]");
        System.exit(1);
    }

    private void helpMessage() {
        System.out.println("-h                display this information and exit");
        System.out.println("-c                clean up directories before running");
        System.out.println("-C                clean and exit");
        System.out.println("-a                employ autocorrect if the gui flag is set");
        System.out.println("-g                start the gui on a local server");
        System.out.println("-t <latitude>     specify starting center point latitude; must be used with -n");
        System.out.println("-n <longitude>    specify starting center point longitude; must be used with -t");

        System.exit(0);
    }

    public static void fatalError(String error) {
        System.out.println("Error: " + error);
        System.exit(1);
    }



    private OptionSet checkArgs(String[] args) {
        OptionParser parser = new OptionParser();

        parser.accepts("a");
        parser.accepts("c");
        parser.accepts("C");
        parser.accepts("g");
        parser.accepts("h");
        parser.accepts("n").withRequiredArg().ofType(Double.class);
        parser.accepts("s").withRequiredArg().ofType(Double.class);

        OptionSet options = null;
        try {
            options = parser.parse(args);
        } catch (Exception e) {
            usageError();
        }
        return options;
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
        checkForFile(directory, REGIONS_FILE_NAME);
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
