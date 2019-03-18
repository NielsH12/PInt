package dk.nielshvid.intermediator;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CapabilityHandler {
    private static HashMap<UUID, Capability> capabilities = new HashMap<>();
    private LocalTime lastClean = LocalTime.now();
    private HashMap<String, Node<String>> treeTemplates = new HashMap<String, Node<String>>(){{
        put("get", new Node<String>("get"){{
            addChild(new Node<String>("retrieve"){{
                addChild(new Node<>("testCase"));
            }});
            addChild(new Node<>("move"));
        }});
    }};



    public UUID addCapability(UUID userID, String key){

        if(!treeTemplates.containsKey(key)){
            return null;
        }

        Node<String> treeTemplate = treeTemplates.get(key);

        Capability capability = new Capability(userID, treeTemplate);
        capabilities.put(capability.getID(),capability);
        return capability.ID;
    }

    public boolean useAction(UUID UserID, UUID CapabilityID, String action){
        if(lastClean.plusHours(24).isBefore(LocalTime.now())){
            cleanCapabilities();
        }

        if(!capabilities.containsKey(CapabilityID)){
           return false;
        }

        boolean result =  capabilities.get(CapabilityID).useAction(UserID, action);
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
//        private ArrayList<String> Actions;
        private Node<String> Actions;
        private UUID ID;
        private UUID userID;

        Capability(UUID userID,  Node<String> Actions){
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

//            if (Actions.get(0).equals(action)) {
//                lastUsed = LocalTime.now();
//
//                Actions.remove(0);
//                return true;
//            }

            Node<String> t = this.Actions.useAction(action);
            if (t != null){
                this.Actions = t;
                return true;
            }
            return false;
        }
    }

    public class Node<T> {

        private T data = null;

        private List<Node<T>> children = new ArrayList<>();

        private Node<T> parent = null;

        public Node(T data) {
            this.data = data;
        }

        public Node<T> addChild(Node<T> child) {
            child.setParent(this);
            this.children.add(child);
            return child;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public Node<T> getParent() {
            return parent;
        }

        public Node<T> useAction(T action){
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

        private void setParent(Node<T> parent) {
            this.parent = parent;
        }
    }
}
