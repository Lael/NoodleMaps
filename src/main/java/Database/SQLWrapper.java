package Database;

import NoodleMaps.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Abstracts out some mySQL stuff. Uses try/catch blocks and returns null or false to reflect failure.
 * Created by Lael Costa on 6/10/16.
 */
public class SQLWrapper {

    /* stringy things */
    private static String jdbcDriver = "org.sqlite.JDBC";
    private static String dbUrlBase = "jdbc:sqlite:";

    /* returns a connection to a database created in the file pointed to by path with name */
    public Connection createNewDB(String path, String name) {
        Connection conn = null;
        try {
            Class.forName(jdbcDriver);
            conn = DriverManager.getConnection(dbUrlBase + path);
            Statement statement = conn.createStatement();

            int result = statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + name);
            if (result == 0) {
                System.out.println("Database exists...");
            }

        } catch (ClassNotFoundException e) {
            Main.printError("Could not find driver class: " + e.getLocalizedMessage());
        } catch (SQLException e) {
            Main.printError("Failed to create or connect to SQL database: " + e.getLocalizedMessage());
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e1){
                Main.printError("Failed to close SQL database connection: " + e.getLocalizedMessage());

            }
        }
        return conn;
    }

    public boolean createTable(Connection conn, String table) {
        return tryStatement(conn, "CREATE TABLE" + table);
    }

    public boolean insertItem(Connection conn, String table, Object values) {
        return tryStatement(conn, "INSERT INTO " + table + " VALUES " + values.toString());
    }

    private boolean tryStatement(Connection conn, String sql) {
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}
