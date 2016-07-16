package NoodleMaps;

import javafx.beans.binding.DoubleExpression;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main2 {

    public static String MAPS_DIR_NAME = "maps_data";
    public static String REGIONS_FILE_NAME = "regions.txt";
    public static String DATABASE_FILE_NAME = "noodle_maps.sqlite3";
    public static String TILES_DIR_NAME = "tiles";
    public static String XML_DIR_NAME = "data";


    public static void main(String[] args) {
        Main2 main2 = new Main2();
        main2.run(args);
    }

    private void run(String[] args) {
        OptionSet options = checkArgs(args);

        System.out.println("Welcome to NoodleMaps!");
        System.out.println("Verifying directory...");

        verifyDirectory();
    }

    private void usageError() {
        System.out.println("Usage: ./NoodleMaps [-acCgh] [-t <lat> -n <lon>] [-s <size>]");
        System.exit(1);
    }

    private void helpMessage() {
        System.out.println("-h                display this information and exit");
        System.out.println("-c                clean up directories before running");
        System.out.println("-C                clean and exit");
        System.out.println("-a                employ autocorrect if the gui flag is set");
        System.out.println("-g                start the gui on a local server");
        System.out.println("-n <longitude>    specify starting center point longitude; must be used with -t");
        System.out.println("-t <latitude>     specify starting center point latitude; must be used with -n");
        System.out.println("-s <size>         specify starting size");

        System.exit(0);
    }

    public void fatalError(String error) {
        System.out.println("Error:" + error);
    }



    private OptionSet checkArgs(String[] args) {
        OptionParser parser = new OptionParser();
        OptionSet options = null;
        parser.accepts("a");
        parser.accepts("c");
        parser.accepts("C");
        parser.accepts("g");
        parser.accepts("h");
        parser.accepts("n").withRequiredArg().ofType(Double.class);
        parser.accepts("s").withRequiredArg().ofType(Double.class);
        parser.accepts("t").withRequiredArg().ofType(Double.class);

        options = parser.parse(args);

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
                    fatalError("Could not create directory \'" + file + "\'!");
                }
            } catch (IOException e) {
                fatalError("Could not create file \'" + file + "\' - " + e.getLocalizedMessage());
            }
        }

        return f;
    }

    private void verifyDirectory() {
        // check that directory exists
        File directory = checkForDirectory(null, MAPS_DIR_NAME);

        // check that directory contains files regions.txt and noodle_maps.sqlite3, and directory tiles
        checkForFile(directory, REGIONS_FILE_NAME);
        checkForFile(directory, DATABASE_FILE_NAME);
        checkForDirectory(directory, TILES_DIR_NAME);
        checkForDirectory(directory, XML_DIR_NAME);

        // TODO: verify tiles and xml directories
    }
}
