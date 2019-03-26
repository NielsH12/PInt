package dk.nielshvid.intermediator;

import java.util.HashMap;

public class PolicyHandler {

    private static HashMap<String, HashMap<String,Condition>> policyMap = new HashMap<String, HashMap<String, Condition>>() {{
        put("Decan", new HashMap<String,Condition>(){{
        }});
        put("Doctor", new HashMap<String,Condition>(){{
            put("Freezer/retrieve", new Condition() {
                @Override
                public boolean evaluate(int x, int y) {
                    return x > -1 && y > -1;
                }
            });
            put("fiskeLars", new Condition() {
                @Override
                public boolean evaluate(int x, int y) {
                    return x > -1 && y > -1;
                }
            });
            put("Freezer/insert",new Condition() {
                @Override
                public boolean evaluate() {
                    return true;
                }
                @Override
                public boolean evaluate(int x, int y) {
                    return true;
                }
            });
            put("BoxDB/retrieve", new Condition() {
                @Override
                public boolean evaluate() {
                    return true;
                }
            });
            put("BoxDB/insert", new Condition() {
                @Override
                public boolean evaluate() {
                    return true;
                }
            });
        }});
        put("Assistant", new HashMap<String,Condition>(){{
        }});
        put("Student", new HashMap<String,Condition>(){{
        }});
    }};

    public boolean authorize(String Role, String Action){
        System.out.println("PolicyHandler.authorize()");
        try {
            System.out.println("\t Authorized");
            return policyMap.get(Role).get(Action).evaluate();
        }
        catch (Exception e){
            System.out.println("\t " + Role + " is not allowed to perform action: " + Action);
            return false;
        }
    }

    public boolean authorize(String Role, String Action, int x, int y){
        System.out.println("PolicyHandler.authorize()");
        try {
//            System.out.println("\t Authorize");
            return policyMap.get(Role).get(Action).evaluate(x, y);
        }
        catch (Exception e){
            System.out.println("\t " + Role + " is not allowed to perform action: " + Action);
            return false;
        }
    }

    private interface Condition {

        default boolean evaluate(int x, int y) {
            return false;
        }

        default boolean evaluate() {
            return false;
        }
    }
}