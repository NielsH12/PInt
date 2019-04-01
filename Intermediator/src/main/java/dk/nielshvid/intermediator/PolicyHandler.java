package dk.nielshvid.intermediator;

import javax.ws.rs.core.MultivaluedMap;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;

public class PolicyHandler{
    private static OraclesInterface oracles;

    public PolicyHandler(OraclesInterface oracles){
        this.oracles = oracles;
    }

    private static HashMap<String, HashMap<String,Condition>> policyMap = new HashMap<String, HashMap<String, Condition>>() {{
        put("Decan", new HashMap<String,Condition>(){{
        }});
        put("Doctor", new HashMap<String,Condition>(){{
            put("Freezer/retrieve", map -> Integer.parseInt(map.getFirst("xPos")) > -1 && Integer.parseInt(map.getFirst("yPos")) > -1);
            put("fiskeLars", map -> Integer.parseInt(map.getFirst("xPos")) > -1 && Integer.parseInt(map.getFirst("yPos")) > -1);
            put("Freezer/insert", map -> toEpoch(oracles.getAccessed("")) < toEpoch(LocalDateTime.now()));
            put("BoxDB/retrieve", map -> true);
            put("BoxDB/insert", map -> true);
        }});
        put("Assistant", new HashMap<String,Condition>(){{
        }});
        put("Student", new HashMap<String,Condition>(){{
        }});
    }};

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

    private static long toEpoch(LocalDateTime dateTime){
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}