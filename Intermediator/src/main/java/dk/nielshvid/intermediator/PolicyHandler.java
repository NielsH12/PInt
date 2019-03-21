package dk.nielshvid.intermediator;

import java.util.HashMap;

public class PolicyHandler {

    private static HashMap<String, HashMap<String,Boolean>> policyMap = new HashMap<String, HashMap<String, Boolean>>() {{
        put("Decan", new HashMap<String,Boolean>(){{
        }});
        put("Doctor", new HashMap<String,Boolean>(){{
            put("Freezer/retrieve", true);
            put("Freezer/insert", true);
            put("BoxDB/retrieve", true);
            put("BoxDB/insert", true);
        }});
        put("Assistant", new HashMap<String,Boolean>(){{
        }});
        put("Student", new HashMap<String,Boolean>(){{
        }});
    }};

    public boolean authorize(String Role, String Action){
        System.out.println("PolicyHandler.authorize()");
        try {
            System.out.println("\t Authorized");
            return policyMap.get(Role).get(Action);
        }
        catch (Exception e){
            System.out.println("\t " + Role + " is not allowed to perform action: " + Action);
            return false;
        }
    }
}