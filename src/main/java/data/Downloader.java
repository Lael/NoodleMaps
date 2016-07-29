package data;

import location.BoundingBox;

import java.io.File;

public class Downloader {

    private static final String URL_BASE = "";
    private static final String FILENAME_BASE = "";

    public static File downloadByBox(BoundingBox box) {
        String url = getUrl(box);
        String fileName = getFileName(box);
    }

    private static String getUrl(BoundingBox boundingBox) {

    }

    private static String getFileName(BoundingBox boundingBox) {

    }
}
