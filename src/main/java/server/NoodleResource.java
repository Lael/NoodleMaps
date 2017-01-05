package server;

import com.codahale.metrics.annotation.Timed;
import data.DrawWay;
import location.LatLon;
import tiles.Tile;

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public String getIndexHTML() {
        System.out.println("Woot!");
        try {
            return new String(Files.readAllBytes(Paths.get("/Users/laelcosta/Desktop/NoodleMaps/web/index.html")));
        } catch (Exception e) {
            return "Oops!";
        }
    }

    @GET
    @Path("/tile/{lat}/{lon}/{zoom}")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public String getTile(@PathParam("lat") double lat,
                                 @PathParam("lon") double lon,
                                 @PathParam("zoom") int zoom) {
        String response = noodleService.fetchTile(zoom, new LatLon(lat, lon));
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
