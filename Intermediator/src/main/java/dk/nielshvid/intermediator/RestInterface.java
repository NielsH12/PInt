package dk.nielshvid.intermediator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/")
public class RestInterface{
    private IdentityService identityService = new IdentityService();
    private Guard guard = new Guard(identityService);

    @Path("Freezer/insert")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertFreezer (@QueryParam("UserID") String UserID, @QueryParam("Capability") UUID Capability, @QueryParam("BoxID") String BoxID, @QueryParam("xPos") int xPos, @QueryParam("yPos") int yPos){


        // Check policies
        if (guard.authorize(UserID, BoxID, Capability, "Freezer/insert")){
            // Forward request
            return IntermediatorClient.insertFreezer(UserID, BoxID, xPos, yPos);
        };

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

    }

    @Path("Freezer/retrieve")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response retrieveFreezer (@QueryParam("UserID") String UserID, @QueryParam("Capability") UUID Capability, @QueryParam("BoxID") String BoxID, @QueryParam("xPos") int xPos, @QueryParam("yPos") int yPos){


        // Check policies
        if (guard.authorize(UserID, BoxID, Capability, "Freezer/retrieve")){
            // Forward request
            return IntermediatorClient.retrieveFreezer(UserID, BoxID, xPos, yPos);
        };

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

    }

    @Path("BoxDB/get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBoxDB (@QueryParam("UserID") String UserID, @QueryParam("Capability") UUID Capability, @QueryParam("BoxID") String BoxID){

        // This structure can be optimized (maybe)
        UUID CapabilityID = guard.generateCapability(UserID, BoxID, "BoxDB/get");

        // Check policies
        if (guard.authorize(UserID, BoxID, Capability, "BoxDB/get")){
            // Forward request
            return Response.fromResponse(IntermediatorClient.getBoxDB(UserID, BoxID)).header("Capability", CapabilityID).build();
        };

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

    }

    @Path("BoxDB/insert")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertBoxDB (@QueryParam("UserID") String UserID, @QueryParam("Capability") UUID Capability, @QueryParam("BoxID") String BoxID, @QueryParam("xPos") int xPos, @QueryParam("yPos") int yPos){


        // Check policies
        if (guard.authorize(UserID, BoxID, Capability, "BoxDB/insert", xPos, yPos)){
            // Forward request
            return IntermediatorClient.insertBoxDB(UserID, BoxID, xPos, yPos);
        };

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

    }

    @Path("BoxDB/retrieve")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response retrieveBoxDB (@QueryParam("UserID") String UserID, @QueryParam("Capability") UUID Capability, @QueryParam("BoxID") String BoxID){


        // Check policies
        if (guard.authorize(UserID, BoxID, Capability, "BoxDB/retrieve")){
            // Forward request
            return IntermediatorClient.retrieveBoxDB(UserID, BoxID);
        };

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

    }

    @Path("BoxDB/findEmptySlot")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response findEmptySlotBoxDB (@QueryParam("UserID") String UserID, @QueryParam("Capability") UUID Capability, @QueryParam("BoxID") String BoxID){

        // This structure can be optimized (maybe)
        UUID CapabilityID = guard.generateCapability(UserID, BoxID, "BoxDB/findEmptySlot");

        // Check policies
        if (guard.authorize(UserID, BoxID, Capability, "BoxDB/findEmptySlot")){
            // Forward request
            return Response.fromResponse(IntermediatorClient.findEmptySlotBoxDB(UserID, BoxID)).header("Capability", CapabilityID).build();
        };

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

    }

    @Path("BoxDB/getID")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIDBoxDB (@QueryParam("UserID") String UserID, @QueryParam("Capability") UUID Capability, @QueryParam("BoxID") String BoxID, @QueryParam("xPos") int xPos, @QueryParam("yPos") int yPos){


        // Check policies
        if (guard.authorize(UserID, BoxID, Capability, "BoxDB/getID")){
            // Forward request
            return IntermediatorClient.getIDBoxDB(UserID, BoxID, xPos, yPos);
        };

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

    }

}