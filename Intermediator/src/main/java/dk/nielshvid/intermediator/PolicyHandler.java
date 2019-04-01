package dk.nielshvid.intermediator;

import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;

public class PolicyHandler {

    private static HashMap<String, HashMap<String,Condition>> policyMap = new HashMap<String, HashMap<String, Condition>>() {{
        put("Decan", new HashMap<String,Condition>(){{
        }});
        put("Doctor", new HashMap<String,Condition>(){{
            put("Freezer/retrieve", map -> Integer.parseInt(map.getFirst("xPos")) > -1 && Integer.parseInt(map.getFirst("yPos")) > -1);
            put("fiskeLars", map -> Integer.parseInt(map.getFirst("xPos")) > -1 && Integer.parseInt(map.getFirst("yPos")) > -1);
            put("Freezer/insert", map -> true);
            put("BoxDB/retrieve", map -> true);
            put("BoxDB/insert", map -> true);
        }});
        put("Assistant", new HashMap<String,Condition>(){{
        }});
        put("Student", new HashMap<String,Condition>(){{
        }});
    }};

//    public boolean authorize(String Role, String Action){
//        System.out.println("PolicyHandler.authorize()");
//        try {
//            System.out.println("\t Authorized");
//            return policyMap.get(Role).get(Action).evaluate();
//        }
//        catch (Exception e){
//            System.out.println("\t " + Role + " is not allowed to perform action: " + Action);
//            return false;
//        }
//    }

    public boolean authorize(String Role, String Action, MultivaluedMap<String, String> map){
        System.out.println("PolicyHandler.authorize()");
        try {
//            System.out.println("\t Authorize");
            return policyMap.get(Role).get(Action).evaluate(map);
        }
        catch (Exception e){
            System.out.println("\t " + Role + " is not allowed to perform action: " + Action);
            return false;
        }
    }

    private interface Condition {
        boolean evaluate(MultivaluedMap<String, String> fisk);
    }
}