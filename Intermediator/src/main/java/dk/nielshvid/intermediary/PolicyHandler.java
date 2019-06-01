package dk.nielshvid.intermediary;

import org.json.JSONException;
import org.json.JSONObject;
import javax.ws.rs.core.MultivaluedMap;
import java.time.LocalDate;
import java.util.HashMap;
import java.time.temporal.ChronoUnit;

public class PolicyHandler {
	private InformationServiceInterface informationService = new InformationService();

	private HashMap<String, HashMap<String, RoleCondition>> rolePolicyMap = new HashMap<String, HashMap<String, RoleCondition>>() {{
		put("Root", new HashMap<String, RoleCondition>(){{ 
		}});
		put("Researcher", new HashMap<String, RoleCondition>(){{ 
			put("Freezer/fisk", (map, body) -> (Integer.parseInt(body.getString("containerSize")) == 81));
		}});
		put("Guest", new HashMap<String, RoleCondition>(){{ 
		}});
		put("Observer", new HashMap<String, RoleCondition>(){{ 
			put("Freezer/get", (map, body) -> true);
		}});
	}};
	
	private HashMap<String, HashMap<String, EntityCondition>> entityPolicyMap = new HashMap<String, HashMap<String, EntityCondition>>() {{
		put("Sample", new HashMap<String,EntityCondition>(){{
		}});
		put("Blood", new HashMap<String,EntityCondition>(){{
		}});
	}};

	public PolicyHandler(){};
	public PolicyHandler(HashMap<String, HashMap<String, RoleCondition>> rolePolicyMap,
						 HashMap<String, HashMap<String, EntityCondition>> entityPolicyMap,
						 InformationServiceInterface serviceInterface){
		this.rolePolicyMap = rolePolicyMap;
		this.entityPolicyMap = entityPolicyMap;
		this.informationService = serviceInterface;
	};


	public boolean roleAuthorize(String Role, String Resource, MultivaluedMap<String, String> map, String body) {
		System.out.println("PolicyHandler.roleAuthorize()");
			
		try {
			//System.out.println("\t Authorize");
			JSONObject jsonOb = null;
			if (body != null && !body.isEmpty()){
				jsonOb = new JSONObject(body);
			}

			HashMap<String, RoleCondition> test1 = rolePolicyMap.get(Role);
			RoleCondition test2 = test1.get(Resource);
			boolean test3 = test2.evaluate(map, jsonOb);
			return test3;
//			return rolePolicyMap.get(Role).get(Resource).evaluate(map, jsonOb);
		} catch (Exception e) {
			System.out.println("\t " + Role + " is not allowed to perform resource: " + Resource);
			System.out.println("Exception: " + e);
			return false;
		}
	}
	
	public boolean entityAuthorize(String Entity, String Resource, MultivaluedMap<String, String> map) {
		System.out.println("PolicyHandler.entityAuthorize()");
		try {
			//System.out.println("\t Authorize");
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
