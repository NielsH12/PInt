package dk.nielshvid.intermediary;

import org.json.JSONException;
import org.json.JSONObject;
import javax.ws.rs.core.MultivaluedMap;
import java.time.LocalDate;
import java.util.HashMap;
import java.time.temporal.ChronoUnit;

public class PolicyHandler {
	public PolicyHandler(){};
	
    public PolicyHandler(HashMap<String, HashMap<String, RoleCondition>> rolePolicyMap,
                         HashMap<String, HashMap<String, EntityCondition>> entityPolicyMap,
                         InformationServiceInterface serviceInterface){
        this.rolePolicyMap = rolePolicyMap;
        this.entityPolicyMap = entityPolicyMap;
        this.informationService = serviceInterface;
    };
	
	private InformationServiceInterface informationService = new InformationService();

	private HashMap<String, HashMap<String, RoleCondition>> rolePolicyMap = new HashMap<String, HashMap<String, RoleCondition>>() {{
		put("Supervisor", new HashMap<String, RoleCondition>(){{ 
			put("Freezer/querySample", (map, body) -> true);
		}});
		put("Assistant", new HashMap<String, RoleCondition>(){{ 
			put("Freezer/querySample", (map, body) -> true);
			put("Freezer/insert", (map, body) -> ((body.getString("bloodtype") ).equals("AB+")));
		}});
		put("Researcher", new HashMap<String, RoleCondition>(){{ 
			put("Freezer/querySample", (map, body) -> true);
			put("Freezer/retrieve", (map, body) -> true);
			put("Freezer/insert", (map, body) -> true);
			put("FFU/logicalsetsGET", (map, body) -> true);
			put("FFU/logicalsetsPUT", (map, body) -> (((body.getString("containerSpec") ).equals("c"))&&((body.getInt("containerSize") ) == 81)));
		}});
		put("Observer", new HashMap<String, RoleCondition>(){{ 
			put("FFU/logicalsetsGET", (map, body) -> true);
			put("FFU/logicalsetsPUT", (map, body) -> (((body.getString("containerSpec") ).equals("a"))&&((body.getInt("containerSize") ) == 64)));
		}});
	}};
	
	private HashMap<String, HashMap<String, EntityCondition>> entityPolicyMap = new HashMap<String, HashMap<String, EntityCondition>>() {{
		put("Sample", new HashMap<String,EntityCondition>(){{
			put("Freezer/retrieve", (map) -> (CompareDates(informationService.getSample(map.getFirst("sampleID")).accessed, LocalDate.now()) > 2));
		}});
	}};
	


	public boolean roleAuthorize(String Role, String Resource, MultivaluedMap<String, String> map, String body) {
		System.out.println("PolicyHandler.roleAuthorize()");
			
		try {
			//System.out.println("\t Authorize");
			JSONObject jsonOb = null;
			if (body != null && !body.isEmpty()){
				jsonOb = new JSONObject(body);
			}
			return rolePolicyMap.get(Role).get(Resource).evaluate(map, jsonOb);
		} catch (Exception e) {
			System.out.println("\t " + Role + " is not allowed to perform resource: " + Resource);
			System.out.println("Exception: " + e);
			return false;
		}
	}
	
	public boolean entityAuthorize(String Entity, String Resource, MultivaluedMap<String, String> map) {
		System.out.println("PolicyHandler.entityAuthorize()");
		try {
			return entityPolicyMap.get(Entity).get(Resource).evaluate(map);
		} catch (Exception e) {
			System.out.println("\t " + Entity + " is not allowed to perform resource: " + Resource);
			return false;
		}
	}
	
	public interface RoleCondition {
		boolean evaluate(MultivaluedMap<String, String> map, JSONObject body) throws JSONException;
	}
	public interface EntityCondition {
		boolean evaluate(MultivaluedMap<String, String> mMap);
	}
	
	public static int CompareDates(LocalDate from, LocalDate to){
		return (int) ChronoUnit.DAYS.between(from, to);
	}
}
