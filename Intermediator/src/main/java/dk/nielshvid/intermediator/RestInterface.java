package dk.nielshvid.intermediator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.UUID;

@Path("/")
public class RestInterface{
    private static HashMap<String, HashMap<String,Boolean>> policyMap = new HashMap<String, HashMap<String, Boolean>>() {{
        put("Doctor", new HashMap<String,Boolean>(){{
            put("get", true);
            put("insert", false);
            put("retrieve", true);
        }});
    }};

    private Guard guard = new Guard(policyMap);
    private IdentityService identityService = new IdentityService();

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

        // capability

        // Forward request
        return IntermediatorClient.insertFreezer(UserID, ID, xPos, yPos);
    }

    @Path("Freezer/retrieve")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response retrieveFreezer (@QueryParam("UserID") String UserID, @QueryParam("xPos") int xPos, @QueryParam("yPos") int yPos){

        // capability

        // Forward request
        return IntermediatorClient.retrieveFreezer(UserID, xPos, yPos);
    }

    @Path("BoxDB/get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBoxDB (@QueryParam("UserID") String UserID, @QueryParam("BoxID") String BoxID){
        String Org = BoxID.substring(37);

        String role = identityService.getRole(UUID.fromString(UserID), UUID.fromString(Org));

        if(!guard.checkAccess(role, "get")){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        // Forward request
        return Response.fromResponse(IntermediatorClient.getBoxDB(UserID, BoxID)).header("cap","fisk").build();
    }

    @Path("BoxDB/insert")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response insertBoxDB (@QueryParam("UserID") String UserID, @QueryParam("BoxID") String BoxID, @QueryParam("xPos") int xPos, @QueryParam("yPos") int yPos){

        String Org = BoxID.substring(37);

        String role = identityService.getRole(UUID.fromString(UserID), UUID.fromString(Org));

        if(!guard.checkAccess(role, "insert")){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        // Forward request
        return IntermediatorClient.insertBoxDB(UserID, BoxID, xPos, yPos);
    }

    @Path("BoxDB/retrieve")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response retrieveBoxDB (@QueryParam("UserID") String UserID, @QueryParam("BoxID") String BoxID){

        String Org = BoxID.substring(37);

        String role = identityService.getRole(UUID.fromString(UserID), UUID.fromString(Org));

        if(!guard.checkAccess(role, "retrieve")){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        return Response.fromResponse(IntermediatorClient.getBoxDB(UserID, BoxID)).header("cap","hund").build();
    }

    @Path("BoxDB/findEmptySlot")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response findEmptySlotBoxDB (@QueryParam("UserID") String UserID){

        // Forward request
        return IntermediatorClient.findEmptySlotBoxDB(UserID);
    }


    @Path("NielsErSej/testcase")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response xxx (@QueryParam("UserID") String UserID, @QueryParam("BoxID") String BoxID){

        

        // Forward request
        return IntermediatorClient.findEmptySlotBoxDB(UserID);
    }
}