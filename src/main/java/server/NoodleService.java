package server;

import autocorrect.Trie;
import data.DrawWay;
import location.LatLon;
import tiles.Tile;
import tiles.TileManager;

import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class NoodleService {

    private Trie trie;

    private int maxLed;
    private int numSuggestions;

    private TileManager tileManager;

    public NoodleService(@NotNull Connection connection,
                         @NotNull Trie trie,
                         int maxLed,
                         int numSuggestions) throws SQLException {
        this.trie = trie;
        this.maxLed = maxLed;
        this.numSuggestions = numSuggestions;
        this.tileManager = new TileManager(connection, trie);
    }

    public List<String> autocorrect(String word) {
        return trie.autocorrect(word, maxLed, numSuggestions);
    }

    public String fetchTile(int zoom, LatLon latLon) {
        System.out.println("Tile request: (" + latLon + ", " + zoom + ").");
        return tileManager.fetchTileData(new Tile(zoom, latLon));
    }
}
