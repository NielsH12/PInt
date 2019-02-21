package dk.nielshvid.intermediator;

import dk.nielshvid.storagemanagement.BoxHandler;
import dk.nielshvid.storagemanagement.dbBox;

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
    public String open(@QueryParam("ID") UUID ID)  {
        if(ID != null) {
            dbBox result = new BoxHandler().GetBoxInfoByID(ID);

            if(result.id == null || result.posX < 0 || result.posY < 0){
                throw new WebApplicationException("JENS");
            }
            String response = IntermediatorClient.OpenBox(ID, result.posX, result.posY);
            if(!response.equals("Success")){
                throw new WebApplicationException("Database can't find box", Response.Status.INTERNAL_SERVER_ERROR);
            }
            return "Success";
        }
        throw new WebApplicationException(Response.Status.BAD_REQUEST);

    }

    @Path("insert")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String insert(@QueryParam("ID") String ID)  {
        return Intermediator.lookup(UUID.fromString(ID));
    }
}
