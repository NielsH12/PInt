package dk.nielshvid.intermediator;

import dk.nielshvid.storagemanagement.BoxHandler;
import dk.nielshvid.storagemanagement.FreezerStateHandler;
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
    @Produces(MediaType.TEXT_PLAIN)
    public String get(@QueryParam("ID") UUID ID)  {
        if(ID != null) {
            String result = Intermediator.lookup(ID);
            if(result != null){
                return result;
            }


            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }
        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    @Path("retrieve")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String retrieve(@QueryParam("ID") UUID ID)  {
        if(ID == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        dbBox result = new BoxHandler().GetBoxInfoByID(ID);

        if(result.id == null || result.posX == null || result.posY == null){
            throw new WebApplicationException(Response.status(204).entity("Box is out of freezer").type("text/plain").build());
        }
        String response = IntermediatorClient.RetrieveBox(ID, result.posX, result.posY);

        if(!response.equals("Success")){
            throw new WebApplicationException(Response.status(500).entity("Database can't find box").type("text/plain").build());
        }
        return "Success";
    }

    @Path("insert")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String insert(@QueryParam("ID") UUID ID)  {
        if(ID == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        dbBox result = new BoxHandler().GetBoxInfoByID(ID);
        if(result.id == null){
            throw new WebApplicationException(Response.status(204).entity("Box do not exist").type("text/plain").build());
        }
        if(result.posX != null || result.posY != null){
            throw new WebApplicationException(Response.status(204).entity("Box is already in Freezer").type("text/plain").build());
        }

        int[] pos = new FreezerStateHandler().FindEmptySlot();
        if(pos == null || pos[0] == -1 || pos[1] == -1){
            throw new WebApplicationException(Response.status(218).entity("Freezer is full").type("text/plain").build());
        }

        String response = IntermediatorClient.InsertBox(ID, pos[0], pos[1]);

        if(!response.equals("Success")){
            throw new WebApplicationException(Response.status(500).entity("Freezer had an error").type("text/plain").build());
        }
        return "Success";
    }
}
