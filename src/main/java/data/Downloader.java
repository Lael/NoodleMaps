package data;

import location.BoundingBox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.DecimalFormat;

public class Downloader {

    private static final String URL_BASE = "";
    private static final String FILENAME_BASE = "";

    private static File downloadBox(BoundingBox box) throws IOException {
        String latLonString = boxToString(box);
        System.out.println(latLonString);

        File mapFile = new File("maps_data/x" + latLonString + ".xml");
        boolean fileExists;

        fileExists = !mapFile.createNewFile();

        if (!fileExists) {
            System.out.println("Attempting to download map data for bounding box " + box.toString() + "...");
            URL url = new URL(URL_BASE + latLonString);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream stream = new FileOutputStream(mapFile);

            long bytesRead = stream.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            System.out.println("Read " + bytesRead + " bytes from OSM...");
            stream.close();
            rbc.close();
        }

        System.out.println("The necessary map data is on disk.");
        return mapFile;
    }

    /**
     *
     * @param box
     * @return
     */
    private static String boxToString(BoundingBox box) {
        DecimalFormat df = new DecimalFormat("#.0");
        return df.format(box.getS()) + "," +
                df.format(box.getW()) + "," +
                df.format(box.getN()) + "," +
                df.format(box.getE());

    }
}
