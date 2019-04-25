package dk.nielshvid.intermediator;

import javax.ws.rs.core.MultivaluedMap;
import java.time.LocalDate;
import java.util.HashMap;
import java.time.temporal.ChronoUnit;

public class PolicyHandler {
    private int CheckCounter = 0;

    private static HashMap<String, HashMap<String, Condition>> rolePolicyMap = new HashMap<String, HashMap<String, Condition>>() {{
        put("Decan", new HashMap<String, Condition>() {{
        }});
        put("Doctor", new HashMap<String, Condition>() {{
            put("Freezer/retrieve", map -> Integer.parseInt(map.getFirst("xPos")) > -1 && Integer.parseInt(map.getFirst("yPos")) > -1);
            put("fiskeLars", map -> Float.parseFloat(map.getFirst("xPos")) > -1 && Integer.parseInt(map.getFirst("yPos")) > -1);
            put("Freezer/insert", map -> true);
            put("BoxDB/retrieve", map -> true);
            put("BoxDB/insert", map -> true);
        }});
        put("Assistant", new HashMap<String, Condition>() {{
        }});
        put("Student", new HashMap<String, Condition>() {{
        }});
    }};

    private static HashMap<String, HashMap<String, EntityCondition>> entityPolicyMap = new HashMap<String, HashMap<String, EntityCondition>>() {{
        put("Sample", new HashMap<String, EntityCondition>() {{
        }});
        put("Sample2", new HashMap<String, EntityCondition>() {{
            put("Freezer/retrieve", (map, counter) -> Integer.parseInt(map.getFirst("xPos")) > -1 && Integer.parseInt(map.getFirst("yPos")) > -1);
            put("fiskeLars", (map, counter) -> Integer.parseInt(map.getFirst("xPos")) > -1 && Integer.parseInt(map.getFirst("yPos")) > -1);
            put("Freezer/insert", (map, counter) -> OracleClient.getSample(map.getFirst("sampleID"), counter).owner.firstName.equals(""));
            put("BoxDB/retrieve", (map, counter) -> true);
            put("BoxDB/insert", (map, counter) -> true);
        }});
        put("Sample3", new HashMap<String, EntityCondition>() {{
        }});
        put("Sample4", new HashMap<String, EntityCondition>() {{
        }});
    }};


    public boolean roleAuthorize(String Role, String Action, MultivaluedMap<String, String> map) {
        System.out.println("PolicyHandler.roleAuthorize()");
        try {
//            System.out.println("\t Authorize");
            return rolePolicyMap.get(Role).get(Action).evaluate(map);
        } catch (Exception e) {
            System.out.println("\t " + Role + " is not allowed to perform action: " + Action);
            return false;
        }
    }

    public boolean entityAuthorize(String Entity, String Action, MultivaluedMap<String, String> map) {
        System.out.println("PolicyHandler.entityAuthorize()");
        CheckCounter++;

        try {
//            System.out.println("\t Authorize");
            return entityPolicyMap.get(Entity).get(Action).evaluate(map, CheckCounter);
        } catch (Exception e) {
            System.out.println("\t " + Entity + " is not allowed to perform action: " + Action);
            return false;
        }
    }

    private interface Condition {
        boolean evaluate(MultivaluedMap<String, String> fisk); //TODO rename
    }
    private interface EntityCondition {
        boolean evaluate(MultivaluedMap<String, String> fisk, int checkCounter); //TODO rename
    }

    private int CompareDates(LocalDate from, LocalDate to){
        LocalDate test = LocalDate.now();
        return (int) ChronoUnit.DAYS.between(from, to);
    }
}