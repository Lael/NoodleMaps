package data;

import location.BoundingBox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.DecimalFormat;

public class Downloader {

    private static final String URL_BASE = "http://overpass-api.de/api/map?bbox=";
    private static final String FILENAME_BASE = "maps_data/data/x";

    /**
     * Downloads a bounding box worth of data from OSM
     * @param box a bounding box to download
     * @return a File object containing the downloaded file
     * @throws IOException on download failure
     */
    public static File downloadBox(BoundingBox box, String id) throws IOException {
        String latLonString = boxToString(box);
        System.out.println(latLonString);

        String fileName = FILENAME_BASE + id + ".xml";
        System.out.println(fileName);

        System.out.println("Attempting to download map data for bounding box " + box.toString() + "...");
        URL url = new URL(URL_BASE + latLonString);
        System.out.println("url.toString() = " + url.toString());
        URLConnection conn = url.openConnection();
        conn.connect();
        HttpURLConnection httpConnection = (HttpURLConnection) conn;
        int code = httpConnection.getResponseCode();
        if (code == 200) {
            ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());
            File mapFile = new File(fileName);
            FileOutputStream stream = new FileOutputStream(mapFile);

            long bytesRead = stream.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            System.out.println("Read " + bytesRead + " bytes from OSM...");
            stream.close();
            rbc.close();
            return mapFile;
        } else {
            System.out.println("Code=" + code);
            return null;
        }
    }

    /**
     * Stringifies a box
     * @param box a bounding box
     * @return a string representation of the box
     */
    private static String boxToString(BoundingBox box) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(box.getS()) + "," +
                df.format(box.getW()) + "," +
                df.format(box.getN()) + "," +
                df.format(box.getE());
    }
}
