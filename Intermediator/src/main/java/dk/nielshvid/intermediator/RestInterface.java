package dk.nielshvid.intermediator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/")
public class RestInterface {
    @Path("hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String u() {
        return "niels";
    }

    @Path("get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@QueryParam("ID") UUID ID)  {
        if(ID != null) {
            String result = Intermediator.lookup(ID);
            if(result != null)
                return result;

            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }
        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    @Path("open")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String open(@QueryParam("ID") String ID)  {
        return Intermediator.lookup(UUID.fromString(ID));
    }

    @Path("insert")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String insert(@QueryParam("ID") String ID)  {
        return Intermediator.lookup(UUID.fromString(ID));
    }
}
