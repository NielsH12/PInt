package dk.nielshvid.intermediator;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Guard {
    private PolicyHandler policyHandler = new PolicyHandler();
    private CapabilityHandler capabilityHandler = new CapabilityHandler();
    private IdentityServiceInterface identityService;

    private static HashSet<String> rolePolicyFreeActions = new HashSet<String>() {{
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

    private static HashMap<String, HashSet> entityPolicyRequiringActions = new HashMap<String, HashSet>(){{
        put("Person", new HashSet<String>(){{
        }});
        put("Sample", new HashSet<String>(){{
            add("Freezer/retrieve");
            add("Freezer/retrieve");
        }});
        put("Pizza", new HashSet<String>(){{
        }});
    }};




    Guard(IdentityServiceInterface identityService){
        this.identityService = identityService;
    }

    public UUID generateCapability(String UserID, String EntityID, String action, MultivaluedMap<String, String> QPmap){
        UUID userID;

        try { userID = UUID.fromString(UserID);
        } catch (Exception e) {
            throw new WebApplicationException("Invalid User ID", Response.Status.BAD_REQUEST);
        }
        String role = identityService.getRole(userID, EntityID);
        if(!rolePolicyFreeActions.contains(action)){
            if(!policyHandler.roleAuthorize(role, action, QPmap)){
                return null;
            }
        }

        if(capabilityRequiringActions.contains(action)){
            return null;
        }

        return capabilityHandler.addCapability(userID, EntityID, action);
    }

    public boolean authorize(String UserID, String EntityID, UUID CapabilityID, String action, MultivaluedMap<String, String> QPmap){
        // Check role policy
        if(!rolePolicyFreeActions.contains(action)){
            String role;
            try {
                role = identityService.getRole(UUID.fromString(UserID), EntityID);
            } catch (Exception e){
                throw new WebApplicationException("Invalid User ID", Response.Status.BAD_REQUEST);
            }
            if(!policyHandler.roleAuthorize(role, action, QPmap)){
                throw new WebApplicationException("Permission denied", Response.Status.FORBIDDEN);
            }
        }

        // Check entity policy
        String entityType = identityService.getEntityType(EntityID);
        if(entityPolicyRequiringActions.get(entityType).contains(action)){
            if(!policyHandler.entityAuthorize(entityType, action, QPmap)){
                throw new WebApplicationException("Permission denied", Response.Status.FORBIDDEN);
            }
        }

        // Check capability
        if(capabilityRequiringActions.contains(action)){
            if(!capabilityHandler.authorize(UUID.fromString(UserID), EntityID, CapabilityID, action)){
                throw new WebApplicationException("Invalid capability", Response.Status.FORBIDDEN);
            }
        }

        return true;
    }
}
