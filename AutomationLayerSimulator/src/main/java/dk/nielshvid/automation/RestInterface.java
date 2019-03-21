package dk.nielshvid.automation;


import org.glassfish.jersey.server.mvc.Viewable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
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

    @Path("retrieve")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String retrieve(@QueryParam("xPos") int x, @QueryParam("yPos") int y)  {
        if (FreezerHandler.updateByPos(x,y,null)){
            return "Success";
        }

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
    }

    @Path("insert")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String insert(@QueryParam("BoxID") String ID, @QueryParam("xPos") int x, @QueryParam("yPos") int y)  {
        if (FreezerHandler.updateByPos(x,y,ID)){
            return "Success";
        }

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
    }
}
