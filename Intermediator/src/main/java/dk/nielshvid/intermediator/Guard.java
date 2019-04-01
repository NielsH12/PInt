package dk.nielshvid.intermediator;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Guard {
    private PolicyHandler policyHandler;
    private CapabilityHandler capabilityHandler = new CapabilityHandler();
    private IdentityServiceInterface identityService;
    private static HashSet<String> publicActions = new HashSet<String>() {{
        add("BoxDB/get");
        add("BoxDB/getID");
        add("BoxDB/findEmptySlot");
    }};
    private static HashSet<String> capabilityRequiringActions = new HashSet<String>() {{
        add("BoxDB/retrieve");
        add("Freezer/retrieve");
        add("Freezer/insert");
        add("BoxDB/insert");
    }};

    Guard(IdentityServiceInterface identityService, OraclesInterface oracles){
        this.identityService = identityService;
        this.policyHandler = new PolicyHandler(oracles);
    }

    public UUID generateCapability(String UserID, String BoxID, String action, MultivaluedMap<String, String> map){
        UUID userID;

        try { userID = UUID.fromString(UserID);
        } catch (Exception e) {
            throw new WebApplicationException("Invalid User ID", Response.Status.BAD_REQUEST);
        }

        String role = identityService.getRole(userID, BoxID);

        if(!publicActions.contains(action)){
            if(!policyHandler.authorize(role, action, map)){
                return null;
            }
        }

        if(capabilityRequiringActions.contains(action)){
            return null;
        }

        return capabilityHandler.addCapability(userID, BoxID, action);
    }

    public boolean authorize(String UserID, String BoxID, UUID CapabilityID, String action, MultivaluedMap<String, String> map){
        if(!publicActions.contains(action)){
            String role;
            try {
                role = identityService.getRole(UUID.fromString(UserID), BoxID);
            } catch (Exception e){
                throw new WebApplicationException("Invalid User ID", Response.Status.BAD_REQUEST);
            }
            if(!policyHandler.authorize(role, action, map)){
                throw new WebApplicationException("Permission denied", Response.Status.FORBIDDEN);
            }
        }

        if(capabilityRequiringActions.contains(action)){
            if(!capabilityHandler.authorize(UUID.fromString(UserID), BoxID, CapabilityID, action)){
                throw new WebApplicationException("Invalid capability", Response.Status.FORBIDDEN);
            }
        }

        return true;
    }

//    public boolean authorize(String UserID, String BoxID, UUID CapabilityID, String action, int x, int y){
//        if(!publicActions.contains(action)){
//            String role;
//            try {
//                role = identityService.getRole(UUID.fromString(UserID), BoxID);
//            } catch (Exception e){
//                throw new WebApplicationException("Invalid User ID", Response.Status.BAD_REQUEST);
//            }
//            if(!policyHandler.authorize(role, action)){ // fix jens
//                throw new WebApplicationException("Permission denied", Response.Status.FORBIDDEN);
//            }
//        }
//
//        if(capabilityRequiringActions.contains(action)){
//            if(!capabilityHandler.authorize(UUID.fromString(UserID), BoxID, CapabilityID, action)){
//                throw new WebApplicationException("Invalid capability", Response.Status.FORBIDDEN);
//            }
//        }
//
//        return true;
//    }
}