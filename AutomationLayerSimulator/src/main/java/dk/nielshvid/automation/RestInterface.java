package dk.nielshvid.automation;

import dk.nielshvid.storagemanagement.dbBox;
import org.glassfish.jersey.server.mvc.Viewable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
public class RestInterface {

    @Path("")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable home() {
        Freezer[][] state = FreezerHandler.GetFreezerState();

        Map<String, String> model = new HashMap<>();
        model.put("s00", state[0][0].htmlString());
        model.put("s01", state[0][1].htmlString());
        model.put("s02", state[0][2].htmlString());

        model.put("s10", state[1][0].htmlString());
        model.put("s11", state[1][1].htmlString());
        model.put("s12", state[1][2].htmlString());

        model.put("s20", state[2][0].htmlString());
        model.put("s21", state[2][1].htmlString());
        model.put("s22", state[2][2].htmlString());

        model.put("s30", state[3][0].htmlString());
        model.put("s31", state[3][1].htmlString());
        model.put("s32", state[3][2].htmlString());

        return new Viewable("/index", model);
    }

    @Path("open")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String open(@QueryParam("xPos") int x, @QueryParam("yPos") int y)  {
        if (FreezerHandler.updateByPos(x,y,null, false)){
            return "success";
        };

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
    }

    @Path("insert")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String insert(@QueryParam("ID") String ID, @QueryParam("xPos") int x, @QueryParam("yPos") int y)  {
        if (FreezerHandler.updateByPos(x,y,ID, true)){
            return "success";
        };

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
    }

    @Path("close")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String close(@QueryParam("xPos") int x, @QueryParam("yPos") int y)  {
        if (FreezerHandler.updateByPos(x,y,null, true)){
            return "success";
        };

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
    }
}
