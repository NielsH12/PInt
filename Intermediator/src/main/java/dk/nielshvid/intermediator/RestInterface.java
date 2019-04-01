package dk.nielshvid.intermediator;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.UUID;

@Path("/")
public class RestInterface{
    private IdentityService identityService = new IdentityService();
    private Oracles oracales = new Oracles();
    private Guard guard = new Guard(identityService, oracales);

    // request skal opfylde den rigtige type på QueryParam, ellers rammer den ikke dette endpoint
    @Path("/query")
    public Response getUsers(@Context UriInfo info, @QueryParam("xPos") int xPos, @QueryParam("yPos") int yPos) {

        String from = info.getQueryParameters().getFirst("from");
        String to = info.getQueryParameters().getFirst("to");

        MultivaluedMap<String, String> queryParamMap = info.getQueryParameters();


        return Response
                .status(200)
                .entity("getUsers is called, from : " + from + ", to : " + to
                        + ", orderBy" ).build();
    }

    @Path("Freezer/insert")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertFreezer (@Context UriInfo info, @QueryParam("UserID") String UserID, @QueryParam("Capability") UUID Capability, @QueryParam("BoxID") String BoxID, @QueryParam("xPos") int xPos, @QueryParam("yPos") int yPos){

        // Check policies
        if (guard.authorize(UserID, BoxID, Capability, "Freezer/insert", info.getQueryParameters())){
            // Forward request
            return IntermediatorClient.insertFreezer(UserID, BoxID, xPos, yPos);
        };

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

    }

    @Path("Freezer/retrieve")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response retrieveFreezer (@Context UriInfo info, @QueryParam("UserID") String UserID, @QueryParam("Capability") UUID Capability, @QueryParam("BoxID") String BoxID, @QueryParam("xPos") int xPos, @QueryParam("yPos") int yPos){

        // Check policies
        if (guard.authorize(UserID, BoxID, Capability, "Freezer/retrieve", info.getQueryParameters())){
            // Forward request
            return IntermediatorClient.retrieveFreezer(UserID, BoxID, xPos, yPos);
        };

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

    }

    @Path("BoxDB/get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBoxDB (@Context UriInfo info, @QueryParam("UserID") String UserID, @QueryParam("Capability") UUID Capability, @QueryParam("BoxID") String BoxID){

        // This structure can be optimized (maybe)
        UUID CapabilityID = guard.generateCapability(UserID, BoxID, "BoxDB/get", info.getQueryParameters());

        // Check policies
        if (guard.authorize(UserID, BoxID, Capability, "BoxDB/get", info.getQueryParameters())){
            // Forward request
            return Response.fromResponse(IntermediatorClient.getBoxDB(UserID, BoxID)).header("Capability", CapabilityID).build();
        };

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

    }

    @Path("BoxDB/insert")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertBoxDB (@Context UriInfo info, @QueryParam("UserID") String UserID, @QueryParam("Capability") UUID Capability, @QueryParam("BoxID") String BoxID, @QueryParam("xPos") int xPos, @QueryParam("yPos") int yPos){

        // Check policies
        if (guard.authorize(UserID, BoxID, Capability, "BoxDB/insert", info.getQueryParameters())){
            // Forward request
            return IntermediatorClient.insertBoxDB(UserID, BoxID, xPos, yPos);
        };

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

    }

    @Path("BoxDB/retrieve")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response retrieveBoxDB (@Context UriInfo info, @QueryParam("UserID") String UserID, @QueryParam("Capability") UUID Capability, @QueryParam("BoxID") String BoxID){

        // Check policies
        if (guard.authorize(UserID, BoxID, Capability, "BoxDB/retrieve", info.getQueryParameters())){
            // Forward request
            return IntermediatorClient.retrieveBoxDB(UserID, BoxID);
        };

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

    }

    @Path("BoxDB/findEmptySlot")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response findEmptySlotBoxDB (@Context UriInfo info, @QueryParam("UserID") String UserID, @QueryParam("Capability") UUID Capability, @QueryParam("BoxID") String BoxID){

        // This structure can be optimized (maybe)
        UUID CapabilityID = guard.generateCapability(UserID, BoxID, "BoxDB/findEmptySlot", info.getQueryParameters());

        // Check policies
        if (guard.authorize(UserID, BoxID, Capability, "BoxDB/findEmptySlot", info.getQueryParameters())){
            // Forward request
            return Response.fromResponse(IntermediatorClient.findEmptySlotBoxDB(UserID, BoxID)).header("Capability", CapabilityID).build();
        };

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

    }

    @Path("BoxDB/getID")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIDBoxDB (@Context UriInfo info, @QueryParam("UserID") String UserID, @QueryParam("Capability") UUID Capability, @QueryParam("BoxID") String BoxID, @QueryParam("xPos") int xPos, @QueryParam("yPos") int yPos){


        // Check policies
        if (guard.authorize(UserID, BoxID, Capability, "BoxDB/getID", info.getQueryParameters())){
            // Forward request
            return IntermediatorClient.getIDBoxDB(UserID, BoxID, xPos, yPos);
        };

        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

    }

}