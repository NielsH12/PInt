package dk.nielshvid.IntermediatorRest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
public class Rest {

    @Path("get")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get(@QueryParam("ID") String ID)
    {

        return "world" + ID;
    }

    @Path("open")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String open(@QueryParam("ID") String ID)
    {


        return "open " + ID;
    }

    @Path("insert")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String insert(@QueryParam("ID") String ID)
    {

        return "insert" + ID;
    }
}
