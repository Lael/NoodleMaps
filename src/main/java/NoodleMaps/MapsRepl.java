package NoodleMaps;

import java.sql.Connection;
import java.util.Scanner;

/**
 * Provides a CLI for NoodleMaps.
 * Created by laelcosta on 6/10/16.
 */
public class MapsRepl implements Runnable {

    private Connection conn;

    public MapsRepl(double lat, double lon, double size, boolean autocorrect) {

        System.out.println("The REPL has been initialized!");
    }

    public void run() {
        System.out.println("Entering the REPL.");

        Scanner scanner = new Scanner(System.in);
        String line;
        System.out.print("> ");
        while (scanner.hasNextLine() &&
                (line = scanner.nextLine()).length() > 0 &&
                line.charAt(line.length() - 1) != 4) {
            System.out.print(line);
            System.out.print("\n> ");
        }
        System.out.println("Exiting the REPL!");
    }
}
