package dk.nielshvid.intermediary;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CapabilityHandler {
    private static HashMap<UUID, Capability> capabilities = new HashMap<>();
    private static LocalTime lastClean = LocalTime.now();
    private static final int CAPABILITY_LIFETIME_SECONDS = 300;
    private HashMap<String, Node<String>> treeTemplates = new HashMap<String, Node<String>>(){{
        put("BoxDB/get", new Node<String>("BoxDB/get"){{
            addChild(new Node<String>("Freezer/retrieve"){{
                addChild(new Node<String>("BoxDB/retrieve"){{
                }});
            }});
        }});
        put("BoxDB/findEmptySlot", new Node<String>("BoxDB/findEmptySlot"){{
            addChild(new Node<String>("Freezer/insert"){{
                addChild(new Node<String>("BoxDB/insert"){{
                }});
            }});
        }});
    }};

    public CapabilityHandler(){}

    public CapabilityHandler(String fisketest){
        if (fisketest == "fisketest") {
            treeTemplates = new HashMap<String, Node<String>>(){{
                put("BoxDB/get", new Node<String>("BoxDB/get"){{
                    addChild(new Node<String>("Freezer/retrieve"){{
                        addChild(new Node<String>("BoxDB/retrieve"){{
                        }});
                    }});
                }});
                put("BoxDB/findEmptySlot", new Node<String>("BoxDB/findEmptySlot"){{
                    addChild(new Node<String>("Freezer/insert"){{
                        addChild(new Node<String>("BoxDB/insert"){{
                        }});
                    }});
                }});
            }};
        }
    }

    public UUID addCapability(String userID, String EntityID, String key){

        if(!treeTemplates.containsKey(key)){
            return null;
        }

        Node<String> treeTemplate = treeTemplates.get(key);

        Capability capability = new Capability(userID, EntityID, treeTemplate);
        capabilities.put(capability.getID(), capability);
        return capability.ID;
    }

    public boolean authorize(String UserID, String EntityID, UUID CapabilityID, String action){
        if(lastClean.plusHours(24).isBefore(LocalTime.now())){
            cleanCapabilities();
        }

        if(!capabilities.containsKey(CapabilityID)){
            return false;
        }

        boolean result =  capabilities.get(CapabilityID).useAction(UserID, EntityID, action);
        if(capabilities.get(CapabilityID).delete()){
            capabilities.remove(CapabilityID);
        }
        return result;
    }

    private void cleanCapabilities(){
        lastClean = LocalTime.now();

        ArrayList<UUID> tempUUIDList = new ArrayList<>(capabilities.keySet());
        for (UUID i : tempUUIDList){
            if (capabilities.get(i).delete()){
                capabilities.remove(i);
            }
        }
    }

    private class Capability {
        private LocalTime lastUsed;
        private Node<String> Actions;
        private UUID ID;
        private String userID;
        private String EntityID;

        Capability(String userID,  String EntityID, Node<String> Actions){
            this.userID = userID;
            this.Actions = Actions;
            this.EntityID = EntityID;

            ID = UUID.randomUUID();
            lastUsed = LocalTime.now();
        }

        UUID getID() {
            return ID;
        }

        boolean delete(){
            LocalTime temp = LocalTime.now();
            if(!temp.isBefore(lastUsed.plusSeconds(CAPABILITY_LIFETIME_SECONDS))){ // debug value
                return true;
            }
            return false;
        }

        boolean useAction(String UserID, String EntityID, String action){
            LocalTime temp = LocalTime.now();

            if(!UserID.equals(this.userID)){
                return false;
            }

            if(!EntityID.equals(this.EntityID)){
                return false;
            }

            if(!temp.isBefore(lastUsed.plusSeconds(CAPABILITY_LIFETIME_SECONDS))){ // debug value
                return false;
            }

            Node<String> t = this.Actions.useAction(action);
            if (t != null){
                this.Actions = t;
                return true;
            }
            return false;
        }
    }

    private class Node<T> {

        private T data = null;
        private List<Node<T>> children = new ArrayList<>();

        Node(T data) {
            this.data = data;
        }

        Node<T> addChild(Node<T> child) {
            this.children.add(child);
            return child;
        }

        Node<T> useAction(T action){
            for (Node n : this.getChildren()){
                if (n.data == action){
                    return n;
                }
            }
            return null;
        }

        private List<Node<T>> getChildren() {
            return children;
        }
    }
}
