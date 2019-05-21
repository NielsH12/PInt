package dk.nielshvid.intermediary;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.time.LocalDate;
import java.util.*;
import java.time.temporal.ChronoUnit;

import static dk.nielshvid.intermediary.Entities.EntityType.*;

public class PolicyHandler {
	private InformationServiceInterface informationService = new InformationService();

	private HashMap<String, HashMap<String, Condition>> rolePolicyMap = new HashMap<String, HashMap<String, Condition>>() {{
		put("Decan", new HashMap<String, Condition>(){{
			put("Freezer/retrieve", map -> (Integer.parseInt(map.getFirst("xPos")) + Integer.parseInt(map.getFirst("yPos")) == 10));
		}});
		put("Doctor", new HashMap<String, Condition>(){{
			put("Freezer/retrieve", map -> true);
			put("Freezer/insert", map -> true);
			put("BoxDB/retrieve", map -> (3 <= 2));
			put("BoxDB/insert", map -> ((Integer.parseInt(map.getFirst("xPos")) == 2)&&(Integer.parseInt(map.getFirst("yPos")) == 0)));
		}});
		put("Assistant", new HashMap<String, Condition>(){{
		}});
		put("Student", new HashMap<String, Condition>(){{
		}});
	}};

	// Jens Idea
	private HashMap<String, HashMap<String, Policy>> newRolePolicyMapThatCanPrintCondition = new HashMap<String, HashMap<String, Policy>>() {{
		put("Doctor", new HashMap<String, Policy>(){{
			put("BoxDB/insert", new Policy("1", map -> ((Integer.parseInt(map.getFirst("xPos")) == 2)&&(Integer.parseInt(map.getFirst("yPos")) == 0))));
			put("BoxDB/2", new Policy("2", map -> ((Integer.parseInt(map.getFirst("xPos")) == 2)&&(Integer.parseInt(map.getFirst("yPos")) == 0))));
			put("BoxDB/3", new Policy("3", map -> ((Integer.parseInt(map.getFirst("xPos")) == 2)&&(Integer.parseInt(map.getFirst("yPos")) == 0))));
			put("BoxDB/4", new Policy("4", map -> ((Integer.parseInt(map.getFirst("xPos")) == 2)&&(Integer.parseInt(map.getFirst("yPos")) == 0))));
		}});
		put("Fisk", new HashMap<String, Policy>(){{
			put("BoxDB/insert", new Policy("1", map -> ((Integer.parseInt(map.getFirst("xPos")) == 2)&&(Integer.parseInt(map.getFirst("yPos")) == 0))));
			put("BoxDB/2", new Policy("xPos == 2 && ..", map -> ((Integer.parseInt(map.getFirst("xPos")) == 2)&&(Integer.parseInt(map.getFirst("yPos")) == 0))));
			put("BoxDB/3", new Policy("3", map -> ((Integer.parseInt(map.getFirst("xPos")) == 2)&&(Integer.parseInt(map.getFirst("yPos")) == 0))));
			put("BoxDB/4", new Policy("4", map -> ((Integer.parseInt(map.getFirst("xPos")) == 2)&&(Integer.parseInt(map.getFirst("yPos")) == 0))));
		}});
	}};

	private HashMap<Entities.EntityType, HashMap<String, Condition>> entityPolicyMap = new HashMap<Entities.EntityType, HashMap<String, Condition>>() {{
		put(PERSON, new HashMap<String, Condition>(){{
		}});
		put(SAMPLE, new HashMap<String, Condition>(){{
			put("Freezer/insert", (map) -> (2 == 2));
			put("Freezer/retrieve", (map) -> (informationService.getSample(map.getFirst("SampleID")).owner.firstName.equals("niels")));
		}});
		put(PIZZA, new HashMap<String, Condition>(){{
		}});
	}};

	public PolicyHandler(){}
	public PolicyHandler(HashMap<String, HashMap<String, Condition>> rolePolicyMap, HashMap<Entities.EntityType, HashMap<String, Condition>> entityPolicyMap, InformationServiceInterface informationService){
		if(rolePolicyMap != null){this.rolePolicyMap = rolePolicyMap;}
		if(entityPolicyMap != null){this.entityPolicyMap = entityPolicyMap;}
		if(informationService != null){this.informationService = informationService;}
	}

	public boolean roleAuthorize(String Role, String Action, MultivaluedMap<String, String> map) {
//		System.out.println("PolicyHandler.roleAuthorize()");
		try {
			//System.out.println("\t Authorize");
			return rolePolicyMap.get(Role).get(Action).evaluate(map);
		} catch (Exception e) {
			System.out.println("\t " + Role + " is not allowed to perform action: " + Action);
			return false;
		}
	}

	public boolean entityAuthorize(Entities.EntityType Entity, String Action, MultivaluedMap<String, String> map) {
//		System.out.println("PolicyHandler.entityAuthorize()");
		try {
//			System.out.println(map.getFirst("ID"));
			return entityPolicyMap.get(Entity).get(Action).evaluate(map);
		} catch (Exception e) {
			System.out.println("\t " + Entity + " is not allowed to perform action: " + Action);
			return false;
		}
	}

	public boolean entityAuthorizeByEntityType(String entityID, String action, Entities.EntityType entityType) {
//		System.out.println("PolicyHandler.entityAuthorize()");
		try {
//			System.out.println(map.getFirst("ID"));

			switch (entityType){
				case SAMPLE : return informationService.getSample(entityID).evaluateByActionKey(action);
				default: {
					System.out.println("\t " + entityType + " is not a known Entity Type");
					return false;
				}
			}
		} catch (Exception e) {
			System.out.println("\t " + entityType + " is not allowed to perform action: " + action);
			return false;
		}
	}

	public boolean entityAuthorizeByEtity(String action, Entities.Entity entity) {
//		System.out.println("PolicyHandler.entityAuthorize()");
		try {
			return entity.evaluateByActionKey(action);
		} catch (Exception e) {
			System.out.println("\t " + entity.getClass().getSimpleName() + " is not allowed to perform action: " + action);
			return false;
		}
	}

	//TODO: do we want this?
	public String[] GetAllPoliciesForRole(String Role){
		List<String> strings = new ArrayList<>();
		newRolePolicyMapThatCanPrintCondition.get(Role).forEach((s, policy) -> strings.add(s + ": " + policy.conditionString));

		return strings.toArray(new String[0]);
	}

	//TODO: do we want this?
	public String[] GetAllRolesWithAccessToAction(String Action){
		List<String> strings = new ArrayList<>();
		for (Map.Entry<String, HashMap<String, Policy>> map: newRolePolicyMapThatCanPrintCondition.entrySet()) {
			for (Map.Entry<String, Policy> s: map.getValue().entrySet()){
				if(Action.equals(s.getKey())){
					strings.add(map.getKey() + ": " + s.getValue().conditionString);
				}
			}
		}
		return strings.toArray(new String[0]);
	}

	public interface Condition {
		boolean evaluate(MultivaluedMap<String, String> mMap);
	}

	//TODO: vi kører med JSONObject!!!
	//TODO: se evt. TestMain
    public interface Condition2 {

        boolean evaluate(MultivaluedMap<String, String> mMap, JSONObject jsonObject);
    }

	// Class to handle printing condition
	public class Policy{
		String conditionString;
		private Condition condition;

		Policy(String conditionString, Condition condition){
			this.conditionString = conditionString;
			this.condition = condition;
		}
		boolean evaluate(MultivaluedMap<String, String> mMap){
			return condition.evaluate(mMap);
		}
	}

	//TODO: remove
	@Test
	public void test(){
//		for (String p : this.GetAllPoliciesForRole("Doctor")) {
//			System.out.println(p);
//		}
	}
	//TODO: remove
	@Test
	public void test2(){
		for (String p : this.GetAllRolesWithAccessToAction("BoxDB/insert")) {
			System.out.println(p);
		}
	}

	static public int CompareDates(LocalDate from, LocalDate to){
		return (int) ChronoUnit.DAYS.between(from, to);
	}

    // !!! dur ikke :(
    // forsøg med body : kan ikke lade sig gøre da Condition2 kan være mange entites og derved kan de ikke bruges i lamda.
    private HashMap<String, HashMap<String, Condition2>> rolePolicyMap3 = new HashMap<String, HashMap<String, Condition2>>() {{
        put("Decan", new HashMap<String, Condition2>(){{
            put("Freezer/retrieve", (map, body) -> (Integer.parseInt(map.getFirst("xPos")) + Integer.parseInt(map.getFirst("yPos")) == 10));
        }});
        put("Doctor", new HashMap<String, Condition2>(){{
            put("Freezer/retrieve", (map, body) -> true);
            put("Freezer/insert", (map, body) -> true);
            put("BoxDB/retrieve", (map, body) -> (1 <= 2));
            put("BoxDB/insert", (map, body) -> ((Integer.parseInt(map.getFirst("xPos")) == 2)&&(Integer.parseInt(map.getFirst("yPos")) == 0)));
        }});
        put("Assistant", new HashMap<String, Condition2>(){{
        }});
        put("Student", new HashMap<String, Condition2>(){{
        }});
    }};
}
