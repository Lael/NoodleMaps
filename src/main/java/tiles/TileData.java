package tiles;

import com.fasterxml.jackson.annotation.JsonProperty;
import data.DrawWay;

import java.util.List;

public class TileData {
    private String id;
    private List<DrawWay> ways;

    TileData(String id, List<DrawWay> ways) {
        this.id = id;
        this.ways = ways;
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty
    public List<DrawWay> getWays() {
        return ways;
    }

    @JsonProperty
    public void setWays(List<DrawWay> ways) {
        this.ways = ways;
    }
}
