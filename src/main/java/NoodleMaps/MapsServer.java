package NoodleMaps;

/**
 * Created by laelcosta on 6/10/16.
 */
public class MapsServer implements Runnable {

    public MapsServer (double lat, double lon, double size, boolean autocorrect) {

        System.out.println("Server initialized.");
    }

    public void run() {
        System.out.println("Server starting up.");
        System.out.println("Server closing down.");
        return;
    }
}
