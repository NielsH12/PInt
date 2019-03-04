package dk.nielshvid.intermediator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class RestInterface{

    @Path("Freezer/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response hello (){
        System.out.println("hello");
        return null;
    }

    @Path("Freezer/insert")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertFreezer (@QueryParam("UserID") String UserID, @QueryParam("ID") String ID, @QueryParam("xPos") int xPos, @QueryParam("yPos") int yPos){

        // Check policies
        if(!AccessHandler.CheckAccess(UserID, "Freezer", "insert")){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        // Forward request
        return IntermediatorClient.insertFreezer(UserID, ID, xPos, yPos);
    }
    
    @Path("Freezer/retrieve")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response retrieveFreezer (@QueryParam("UserID") String UserID, @QueryParam("xPos") int xPos, @QueryParam("yPos") int yPos){

        // Check policies
        if(!AccessHandler.CheckAccess(UserID, "Freezer", "retrieve")){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        // Forward request
        return IntermediatorClient.retrieveFreezer(UserID, xPos, yPos);
    }

    @Path("BoxDB/get")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getBoxDB (@QueryParam("UserID") String UserID, @QueryParam("ID") String ID){

        // Forward request
        return IntermediatorClient.getBoxDB(UserID, ID);
    }

    @Path("BoxDB/insert")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertBoxDB (@QueryParam("UserID") String UserID, @QueryParam("ID") String ID, @QueryParam("xPos") int xPos, @QueryParam("yPos") int yPos){

        // Check policies
        if(!AccessHandler.CheckAccess(UserID, "BoxDB", "insert")){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        // Forward request
        return IntermediatorClient.insertBoxDB(UserID, ID, xPos, yPos);
    }

    @Path("BoxDB/retrieve")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveBoxDB (@QueryParam("UserID") String UserID, @QueryParam("ID") String ID){

        // Check policies
        if(!AccessHandler.CheckAccess(UserID, "BoxDB", "retrieve")){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        // Forward request
        return IntermediatorClient.retrieveBoxDB(UserID, ID);
    }

    @Path("BoxDB/findEmptySlot")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response findEmptySlotBoxDB (@QueryParam("UserID") String UserID){

        // Forward request
        return IntermediatorClient.findEmptySlotBoxDB(UserID);
    }
}