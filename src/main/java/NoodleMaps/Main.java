package NoodleMaps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Maps.BoundingBox;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Main class of the NoodleMaps project. Parses command line arguments and downloads necessary data
 * Created by Lael Costa on 6/10/16.
 */
public class Main {

    private static double lat = 41.75;
    private static double lon = -71.45;
    private static double size = 0.125;
    private static boolean autocorrect = false;

    private static final String OSM_BASE_URL = "http://overpass-api.de/api/map?bbox=";

    public static void main(String[] args) {
        System.out.println("Welcome to NoodleMaps!");

        OptionParser parser = new OptionParser();

        parser.accepts("l").withRequiredArg().ofType(Double.class).withValuesSeparatedBy(',');
        parser.accepts("s").withRequiredArg().ofType(Double.class);
        parser.accepts("a");
        parser.accepts("gui");

        OptionSet options = null;
        try {
            options = parser.parse(args);
        } catch (Exception e) {
            usageError();
        }

        /* make these round to, say, 3 decimal places? */
        if (options.has("l")) {
            List<Double> coords = (List<Double>) options.valuesOf("l");
            if (coords.size() == 2) {
                lat = coords.get(0);
                lon = coords.get(1);
            } else {
                usageError();
            }
        }

        if (options.has("s")) {
            size = (Double) options.valueOf("s");
        }

        BoundingBox box = null;
        try {
            box = new BoundingBox(lat, lon, size);
        } catch (IllegalArgumentException e) {
            fatalError(e.getLocalizedMessage());
        }


        /* so we have a reasonable square of land to look at */
        downloadData();

        DBMaker dbm = new DBMaker("map_db.sqlite3");
        System.out.println(dbm.makeDB(box));

        if (options.has("a")) {
            autocorrect = true;
        }

        Runnable runnable;
        if (options.has("gui")) {
            runnable = new MapsServer(lat, lon, size, autocorrect);
        } else {
            runnable = new MapsRepl(lat, lon, size, autocorrect);
        }

        runnable.run();
    }

    private static void usageError() {
        System.out.println("Usage: \"./NoodleMaps [-l lat lon] [-s size] [-a] [gui]\"");
        System.out.println("\t - \'lat\' and \'lon\' must represent a valid pair of coordinates");
        System.out.println("\t - size must be a decimal number in [0.001, 0.5)");

        System.exit(1);
    }

    private static boolean validSize(double size) {
        return (size >= 0.001 && size < 0.5);
    }

    public static void printError(String s) {
        System.out.println("Error: " + s);
    }

    private static void fatalError(String s) {
        printError(s);
        System.exit(1);
    }

    private static void downloadData() {
        DecimalFormat df = new DecimalFormat("#.000");
        String latLonString =
                df.format(lon) + "," + df.format(lat) + "," + df.format(lon + size) + "," + df.format(lat + size);

        File mapsDir = new File("map_data");
        File mapFile = new File("map_data/map_data" + latLonString + ".xml");

        if (!mapsDir.exists()) {
            System.out.println("Creating ~/.NoodleMaps...");
            if (!mapsDir.mkdir())
                fatalError("Could not find or create map_data directory.");
        }

        boolean fileExists = false;

        try {
            fileExists = !mapFile.createNewFile();
        } catch (IOException e) {
            fatalError("Failed to create map_data.xml" + e.getLocalizedMessage());
        }

        if (!fileExists) try {
            System.out.println("Attempting to download map data for bounding box "
                    + lat + ", " + lon + ", " + (lat + size) + ", " + (lon + size) + "...");
            URL url = new URL(OSM_BASE_URL + latLonString);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream stream = new FileOutputStream(mapFile);

            long bytesRead = stream.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            System.out.println("Read " + bytesRead + " bytes from OSM...");
            stream.close();
            rbc.close();
        } catch (FileNotFoundException e) {
            fatalError("Failed to create an output stream to map_data.xml" + e.getLocalizedMessage());
        } catch (MalformedURLException e) {
            fatalError("Failed to create URL: " + e.getLocalizedMessage());
        } catch (IOException e) {
            fatalError("Failed to connect to OSM: " + e.getLocalizedMessage());
        }

        verifyData(mapFile);
        System.out.println("The necessary map data is on disk.");
    }

    private static void verifyData(File mapFile) {
        /* read the first few lines and make sure there's one that says the bounds are correct */
        return;
    }
}
