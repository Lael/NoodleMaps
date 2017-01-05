package noodleMaps;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class NoodleMapsConfiguration extends Configuration {
    private Double startLat;

    private Double startLon;

    private Integer startZoom;

    private Integer maxLed;

    private Integer numSuggestions;

    @JsonProperty
    public Double getStartLat() {
        return startLat;
    }

    @JsonProperty
    public void setStartLat(Double startLat) {
        this.startLat = startLat;
    }

    @JsonProperty
    public Double getStartLon() {
        return startLon;
    }

    @JsonProperty
    public void setStartLon(Double startLon) {
        this.startLon = startLon;
    }

    @JsonProperty
    public Integer getStartZoom() {
        return startZoom;
    }

    @JsonProperty
    public void setStartZoom(Integer startZoom) {
        this.startZoom = startZoom;
    }

    @JsonProperty
    public Integer getMaxLed() {
        return maxLed;
    }

    @JsonProperty
    public void setMaxLed(Integer maxLed) {
        this.maxLed = maxLed;
    }

    @JsonProperty
    public Integer getNumSuggestions() {
        return numSuggestions;
    }

    @JsonProperty
    public void setNumSuggestions(Integer numSuggestions) {
        this.numSuggestions = numSuggestions;
    }
}
