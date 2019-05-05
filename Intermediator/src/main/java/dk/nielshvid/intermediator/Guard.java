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
    private InformationServiceInterface informationService = new InformationService();

    private static HashSet<String> rolePolicyFreeActions = new HashSet<String>() {{
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
        }});
        put("Pizza", new HashSet<String>(){{
        }});
    }};

    public Guard(){}
    public Guard(InformationServiceInterface informationService, HashSet<String> rolePolicyFreeActions,
                 HashSet<String> capabilityRequiringActions, HashMap<String, HashSet> entityPolicyRequiringActions,
                 PolicyHandler policyHandler, CapabilityHandler capabilityHandler){
        if(informationService != null){this.informationService = informationService;}
        if(rolePolicyFreeActions != null){
            Guard.rolePolicyFreeActions = rolePolicyFreeActions;}
        if(capabilityRequiringActions != null){
            Guard.capabilityRequiringActions = capabilityRequiringActions;}
        if(entityPolicyRequiringActions != null){
            Guard.entityPolicyRequiringActions = entityPolicyRequiringActions;}
        if(policyHandler != null){
            this.policyHandler = policyHandler;
        }
        if(capabilityHandler != null){
            this.capabilityHandler = capabilityHandler;
        }
    }

    public UUID generateCapability(String UserID, String EntityID, String action, MultivaluedMap<String, String> QPmap){

        String role = informationService.getRole(UserID, EntityID);
        if(!rolePolicyFreeActions.contains(action)){
            if(!policyHandler.roleAuthorize(role, action, QPmap)){
                return null;
            }
        }

        if(capabilityRequiringActions.contains(action)){
            return null;
        }

        return capabilityHandler.addCapability(UserID, EntityID, action);
    }

    public boolean authorize(String UserID, String EntityID, UUID CapabilityID, String action, MultivaluedMap<String, String> QPmap){
        // Check role policy
        if(!rolePolicyFreeActions.contains(action)){
            String role;
            try {
                role = informationService.getRole(UserID, EntityID);
            } catch (Exception e){
                throw new WebApplicationException("Invalid User ID", Response.Status.BAD_REQUEST);
            }
            if(!policyHandler.roleAuthorize(role, action, QPmap)){
                throw new WebApplicationException("RP: Permission denied", Response.Status.FORBIDDEN);
            }
        }

        // Check entity policy
        String entityType = informationService.getEntityType(EntityID);
        if(entityPolicyRequiringActions.get(entityType).contains(action)){
            if(!policyHandler.entityAuthorize(entityType, action, QPmap)){
                throw new WebApplicationException("EP: Permission denied", Response.Status.FORBIDDEN);
            }
        }

        // Check capability
        if(capabilityRequiringActions.contains(action)){
            if(!capabilityHandler.authorize(UserID, EntityID, CapabilityID, action)){
                throw new WebApplicationException("CP: Invalid capability", Response.Status.FORBIDDEN);
            }
        }

        return true;
    }
}
