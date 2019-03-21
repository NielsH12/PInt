package dk.nielshvid.intermediator;
import com.sun.glass.ui.View;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.UUID;

public class Guard {
    private PolicyHandler policyHandler = new PolicyHandler();
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

    Guard(IdentityServiceInterface identityService){
        this.identityService = identityService;
    }

    public UUID generateCapability(String UserID, String BoxID, String action){
        System.out.println("Guard.generateCapability()");
        UUID userID;

        try { userID = UUID.fromString(UserID);
        } catch (Exception e) {
            System.out.println("\t User ID: " + UserID + " couldn't be converted to type UUID");
            throw new WebApplicationException("Invalid User ID", Response.Status.BAD_REQUEST);
        }

        String role = identityService.getRole(userID, BoxID);
        System.out.println("\t role gotten from identityService: " + role);

        if(!publicActions.contains(action)){
            System.out.println("\t Action is not public");
            if(!policyHandler.authorize(role, action)){
                System.out.println("\t Action couldn't be autorized");
                return null;
            }
        }

        if(capabilityRequiringActions.contains(action)){
            System.out.println("\t Action requires capability: can't be used to generate capability");
            return null;
        }


        UUID uid = capabilityHandler.addCapability(userID, BoxID, action);
        System.out.println("\t Capability ID: " + uid);
        return uid;
    }

    public boolean authorize(String UserID, String BoxID, UUID CapabilityID, String action){
        if(!publicActions.contains(action)){
            String role;
            try {
                role = identityService.getRole(UUID.fromString(UserID), BoxID);
            } catch (Exception e){
                throw new WebApplicationException("Invalid User ID", Response.Status.BAD_REQUEST);
            }
            if(!policyHandler.authorize(role, action)){
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
}