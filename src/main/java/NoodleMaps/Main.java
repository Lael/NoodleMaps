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

    private static final String OSM_BASE_URL = "http://overpass-api.de/api/map?bbox=";

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();

        parser.accepts("l").withRequiredArg().ofType(Double.class).withValuesSeparatedBy(' ');
        parser.accepts("s").withRequiredArg().ofType(Double.class);
        parser.accepts("a");
        parser.accepts("gui");

        OptionSet options = parser.parse(args);
        
        if (options.has("l")) {
            List<> coords = options.valuesOf("l");
        }
        if (options.has("s")) {
            double newSize = options.valueOf("s");
        }



        if (options.has("a")) {

        }
        if (options.has("gui")) {

        }
    }

    public static void main2(String[] args) {
        /* parse command line arguments */
        /* downloads OSM data */
        /* parses into SQL data */
        /* create KD-tree from data, if present */
        /* create auto-correct resources */
        /* create graph */
        /* run server */

        /* args: starting region */
        if (args.length != 3 && args.length != 4)
            usageError();

        try {
            lat = Double.parseDouble(args[0]);
            lon = Double.parseDouble(args[1]);
            if (args.length == 4)
                size = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            usageError();
        }

        if (!validLatLon(lat, lon))
            usageError();

        if (!validSize(size))
            usageError();

        /* set up database, etc */
        /* attempt to get the result of http://api.openstreetmap.org/api/0.6/map?bbox=lat,lon,lat+1,lon+1 */
        downloadData();

        int retval = 0;

        switch (args[args.length - 1])  {
            case "gui":
                /* start server */
                MapsServer server = new MapsServer();
                retval = server.run();
                break;
            case "nogui":
                /* start CLI */
                Repl repl = new Repl();
                retval = repl.run();
                break;
            default:
                usageError();
                break;
        }

        System.exit(retval);
    }

    private static void usageError() {
        System.out.println("Usage: \"./NoodleMaps lat lon size] {gui, nogui}\"");
        System.out.println("\t - \'lat\' and \'lon\' must represent a valid pair of coordinates");
        System.out.println("\t - the optional size must be a decimal number in [0.01, 0.5)");

        System.exit(1);
    }

    private static boolean validLatLon(double lat, double lon) {
        return (lat > -180 && lat <= 180 - size && lon > -90 || lon <= 90 - size);
    }

    private static boolean validSize(double size) {
        return (size >= 0.01 && size < 0.5);
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
        System.out.println("The necessary map data has been downloaded.");
    }

    private static void verifyData(File mapFile) {
        /* read the first few lines and make sure there's one that says the bounds are correct */
        return;
    }
}
