package dk.nielshvid.intermediator;

import dk.nielshvid.storagemanagement.BoxHandler;
import dk.nielshvid.storagemanagement.FreezerStateHandler;
import dk.nielshvid.storagemanagement.dbBox;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/")
public class RestInterface {
    private static final String REST_URI = "http://localhost:8081/";
    private static Client client = ClientBuilder.newClient();

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

        int[] pos = FreezerStateHandler.FindEmptySlot();
        if(pos == null || pos[0] == -1 || pos[1] == -1){
            throw new WebApplicationException(Response.status(218).entity("Freezer is full").type("text/plain").build());
        }

        String response = IntermediatorClient.InsertBox(ID, pos[0], pos[1]);

        if(!response.equals("Success")){
            throw new WebApplicationException(Response.status(500).entity("Freezer had an error").type("text/plain").build());
        }

        FreezerStateHandler.InsertID(ID, pos[0], pos[1]);

        return "Success";
    }

    @Path("getBoxPosition")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getBoxPosition(@QueryParam("ID") UUID ID)  {
        if(ID == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        String result = IntermediatorClient.getBoxPosition(ID);

        return result;
    }

    @Path("getBoxInfoByID")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getBoxInfoByID(@QueryParam("ID") UUID ID, @QueryParam("UserID") UUID UserID)  {
        if(ID == null || UserID == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

//      ACCESSCHECK
        if(!AccessHandler.CheckAccess(UserID, "Box", "get")){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        return IntermediatorClient.getBoxInfoByID(ID);
    }

    @Path("findEmptySlot")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String findEmptySlot(@QueryParam("ID") UUID ID)  {

       return IntermediatorClient.findEmptySlot();
    }

    @Path("insertBox")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String insertBox(@QueryParam("ID") UUID ID, @QueryParam("posX") int x, @QueryParam("posY") int y)  {
        return IntermediatorClient.InsertBox(ID, x, y);
    }


    @Path("insertAuto")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String insertAuto(@QueryParam("ID") UUID ID, @QueryParam("posX") int x, @QueryParam("posY") int y)  {
        return IntermediatorClient.InsertBoxAuto(ID, x, y);
    }
}
