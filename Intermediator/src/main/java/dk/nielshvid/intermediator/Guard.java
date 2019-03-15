package dk.nielshvid.intermediator;

import java.util.HashMap;

public class Guard {

    private static HashMap<String, HashMap<String,Boolean>> policyMap;

    public Guard(HashMap<String, HashMap<String,Boolean>> PolicyMap){
        policyMap = PolicyMap;
    }

    public boolean checkAccess(String Role, String Action){
        try {
            return policyMap.get(Role).get(Action);
        }
        catch (Exception e){
            return false;
        }
    }


    private static HashMap<String, HashMap<String,Boolean>> policyMap2 = new HashMap<String, HashMap<String, Boolean>>() {{
        put("Doctor", new HashMap<String,Boolean>(){{
            put("Get", true);
            put("Insert", false);
            put("Delete", 2 < 3);
        }});
    }};
}
