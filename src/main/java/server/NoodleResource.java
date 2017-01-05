package server;

import com.codahale.metrics.annotation.Timed;
import data.DrawWay;
import location.LatLon;
import tiles.Tile;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/noodle")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NoodleResource {

    private NoodleService noodleService;

    public NoodleResource(NoodleService noodleService) {
        this.noodleService = noodleService;
    }

    @GET
    @Path("/tile/{lat}/{lon}/{zoom}")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public List<DrawWay> getTile(@PathParam("lat") double lat,
                                 @PathParam("lon") double lon,
                                 @PathParam("zoom") int zoom) {
        return noodleService.fetchTile(zoom, new LatLon(lat, lon));
    }

    @GET
    @Path("/autocorrect")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public List<String> autocorrect(@QueryParam("word") String word) {
        return noodleService.autocorrect(word);
    }
}
