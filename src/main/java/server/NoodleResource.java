package server;

import com.codahale.metrics.annotation.Timed;
import data.DrawWay;
import location.LatLon;
import tiles.TileData;

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
    @Path("/root")
    @Produces(MediaType.TEXT_HTML)
    @Timed
    public String root() {
        return "Oops!";
    }

    @GET
    @Path("/tile/{lat}/{lon}")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public TileData getTile(@PathParam("lat") double lat,
                            @PathParam("lon") double lon) {
        TileData response = noodleService.fetchTile(new LatLon(lat, lon));
        System.out.println("Done!");
        return response;
    }

    @GET
    @Path("/autocorrect")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public List<String> autocorrect(@QueryParam("word") String word) {
        return noodleService.autocorrect(word);
    }
}
