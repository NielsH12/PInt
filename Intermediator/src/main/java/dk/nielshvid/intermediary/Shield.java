package dk.nielshvid.intermediary;
import com.google.gson.JsonObject;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Shield {
    private PolicyHandler policyHandler = new PolicyHandler();
    private CapabilityHandler capabilityHandler = new CapabilityHandler();
    private InformationServiceInterface informationService = new InformationService();

    private static HashSet<String> rolePolicyFreeActions = new HashSet<String>() {{
        add("biostore/authenticate/login");
        add("biostore/get");
        add("biostore/set");
    }};

    private static HashSet<String> capabilityRequiringActions = new HashSet<String>() {{
        add("BoxDB/retrieve");
        add("Freezer/retrieve");
        add("Freezer/insert");
        add("BoxDB/insert");
    }};

    private static HashMap<Entities.EntityType, HashSet> entityPolicyRequiringActions = new HashMap<Entities.EntityType, HashSet>(){{
        put(Entities.EntityType.PERSON, new HashSet<String>(){{

        }});
        put(Entities.EntityType.SAMPLE, new HashSet<String>(){{
        }});
        put(Entities.EntityType.PIZZA, new HashSet<String>(){{
        }});
    }};

    public Shield(){}
    public Shield(InformationServiceInterface informationService, HashSet<String> rolePolicyFreeActions,
                  HashSet<String> capabilityRequiringActions, HashMap<Entities.EntityType, HashSet> entityPolicyRequiringActions,
                  PolicyHandler policyHandler, CapabilityHandler capabilityHandler){
        if(informationService != null){this.informationService = informationService;}
        if(rolePolicyFreeActions != null){
            Shield.rolePolicyFreeActions = rolePolicyFreeActions;}
        if(capabilityRequiringActions != null){
            Shield.capabilityRequiringActions = capabilityRequiringActions;}
        if(entityPolicyRequiringActions != null){
            Shield.entityPolicyRequiringActions = entityPolicyRequiringActions;}
        if(policyHandler != null){
            this.policyHandler = policyHandler;
        }
        if(capabilityHandler != null){
            this.capabilityHandler = capabilityHandler;
        }
    }

    public UUID generateCapability(String action, MultivaluedMap<String, String> QPmap, String body){


        String role = informationService.getRole(QPmap.getFirst("UserID"), QPmap.getFirst("EntityID"));
        if(!rolePolicyFreeActions.contains(action)){
            if(!policyHandler.roleAuthorize(role, action, QPmap, body)){
                return null;
            }
        }

        if(capabilityRequiringActions.contains(action)){
            return null;
        }

        return capabilityHandler.addCapability(QPmap.getFirst("UserID"), QPmap.getFirst("EntityID"), action);
    }

    public boolean authorize(String action, MultivaluedMap<String, String> QPmap, String body){

        // Check role policy
        rolePolicyCheckWithEntityID(action, QPmap, body);

        // Check entity policy
        entityPolicyCheckWithQPmap(action, QPmap);

        // Check capability
        capabilityPolicyCheck(action, QPmap);

        return true;
    }

    public boolean authorize(String action, MultivaluedMap<String, String> QPmap, Entities.Entity entity, String body){

        // Check role policy
        rolePolicyCheckWithEntity(action, QPmap, entity, body);

        // Check entity policy
        entityPolicyCheckWithEntity(action, entity);

        // Check capability
        capabilityPolicyCheck(action, QPmap);

        return true;
    }

    private void rolePolicyCheckWithEntityID(String action, MultivaluedMap<String, String> QPmap, String body){
        // Check role policy
        if(!rolePolicyFreeActions.contains(action)){
            String UserID = QPmap.getFirst("UserID");
            String EntityID = QPmap.getFirst("EntityID");
            String role;
            try {
                role = informationService.getRole(UserID, EntityID);

                JSONObject jsonOb = null;
                try {
                    jsonOb = new JSONObject(body);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e){
                throw new WebApplicationException("Invalid User ID", Response.Status.BAD_REQUEST);
            }
            if(!policyHandler.roleAuthorize(role, action, QPmap, body)){
                throw new WebApplicationException("RP: Permission denied", Response.Status.FORBIDDEN);
            }
        }
    }

    private void rolePolicyCheckWithEntity(String action, MultivaluedMap<String, String> QPmap, Entities.Entity Entity, String body){
        // Check role policy
        if(!rolePolicyFreeActions.contains(action)){
            String role;
            try {
                role = informationService.getRole(QPmap.getFirst("UserID"), QPmap.getFirst("EntityID"));
            } catch (Exception e){
                throw new WebApplicationException("Invalid User ID", Response.Status.BAD_REQUEST);
            }
            //TODO: body/entity bliver ikke brut i checket!!!
            if(!policyHandler.roleAuthorize(role, action, QPmap, body)){
                throw new WebApplicationException("RP: Permission denied", Response.Status.FORBIDDEN);
            }
        }
    }

    // typen kender man nok???
    private void entityPolicyCheckWithQPmap(String action, MultivaluedMap<String, String> QPmap){
        // Check entity policy
        String EntityID = QPmap.getFirst("EntityID");
        Entities.EntityType entityType = informationService.getEntityType(EntityID);
        if(entityPolicyRequiringActions.get(entityType).contains(action)){
            if(!policyHandler.entityAuthorize(entityType, action, QPmap)){
                throw new WebApplicationException("EP: Permission denied", Response.Status.FORBIDDEN);
            }
        }
    }

    // typen kender man nok???
    private void entityPolicyCheckWithEntityID(String action, String EntityID){
        // Check entity policy
        Entities.EntityType entityType = informationService.getEntityType(EntityID);
        if(entityPolicyRequiringActions.get(entityType).contains(action)){
            if(!policyHandler.entityAuthorizeByEntityType(EntityID, action, entityType)){
                throw new WebApplicationException("EP: Permission denied", Response.Status.FORBIDDEN);
            }
        }
    }

    private void entityPolicyCheckWithEntity(String action, Entities.Entity entity){
        // Check entity policy
        String entityType = entity.getClass().getSimpleName();

        if(entityPolicyRequiringActions.get(entityType).contains(action)){
            if(!policyHandler.entityAuthorizeByEtity(action, entity)){
                throw new WebApplicationException("EP: Permission denied", Response.Status.FORBIDDEN);
            }
        }
    }

    private void capabilityPolicyCheck(String action, MultivaluedMap<String, String> QPmap){
        // Check capability
        String UserID = QPmap.getFirst("UserID");
        String EntityID = QPmap.getFirst("EntityID");
        String _UUID = QPmap.getFirst("CapabilityID");

        if(UserID == null || EntityID == null || _UUID == null){
            throw new WebApplicationException("CP: Missing input", Response.Status.BAD_REQUEST);
        }
        UUID CapabilityID = UUID.fromString(_UUID);


        if(capabilityRequiringActions.contains(action)){
            if(!capabilityHandler.authorize(UserID, EntityID, CapabilityID, action)){
                throw new WebApplicationException("CP: Invalid capability", Response.Status.FORBIDDEN);
            }
        }
    }
}
