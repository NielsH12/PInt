package dk.nielshvid.intermediator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CapabilityHandler {
    private static HashMap<UUID, Capability> capabilities = new HashMap<>();
    private LocalTime lastClean = LocalTime.now();

    public void addCapability(UUID userID, ArrayList<String> Actions){
        Capability capability = new Capability(userID, Actions);
        capabilities.put(capability.getID(),capability);
    }

    public boolean useAction(UUID Userid, UUID CapabilityID, String action){
        if(lastClean.plusHours(24).isBefore(LocalTime.now())){
            cleanCapabilities();
        }

        if(!capabilities.containsKey(CapabilityID)){
           return false;
        }

        boolean result =  capabilities.get(CapabilityID).useAction(Userid, action);
        if(capabilities.get(CapabilityID).delete()){
            capabilities.remove(CapabilityID);
        }
        return result;
    }

    private void cleanCapabilities(){
        lastClean = LocalTime.now();
        for (UUID i : capabilities.keySet()){
            if (capabilities.get(i).delete()){
                capabilities.remove(i);
            }
        }
    }


    public class Capability {
        private LocalTime lastUsed;
        private ArrayList<String> Actions;
        private UUID ID;
        private UUID userID;

        Capability(UUID userID, ArrayList<String> Actions){
            this.userID = userID;
            this.Actions = Actions;

            ID = UUID.randomUUID();
            lastUsed = LocalTime.now();
        }

        public UUID getID() {
            return ID;
        }

        boolean delete(){
            LocalTime temp = LocalTime.now();
            if(!temp.isBefore(lastUsed.plusSeconds(5))){
                return true;
            }
            return false;
        }

        boolean useAction(UUID UserID, String action){
            LocalTime temp = LocalTime.now();

            if(!UserID.equals(this.userID)){
                return false;
            }

            if(!temp.isBefore(lastUsed.plusSeconds(5))){
                return false;
            }

            if (Actions.get(0).equals(action)) {
                lastUsed = LocalTime.now();

                Actions.remove(0);
                return true;
            }
            return false;
        }
    }
}
