package dk.nielshvid.intermediator;

import org.testng.Assert;
import org.testng.annotations.Test;

import javax.ws.rs.core.MultivaluedMap;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class PolicyHandler {
	private InformationServiceInterface informationService = new InformationService();

	private HashMap<String, HashMap<String, Condition>> rolePolicyMap = new HashMap<String, HashMap<String, Condition>>() {{
		put("Decan", new HashMap<String, Condition>(){{
		}});
		put("Doctor", new HashMap<String, Condition>(){{
			put("Freezer/retrieve", map -> true);
			put("Freezer/insert", map -> true);
			put("BoxDB/retrieve", map -> (1 <= 2));
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
			put("BoxDB/2", new Policy("2", map -> ((Integer.parseInt(map.getFirst("xPos")) == 2)&&(Integer.parseInt(map.getFirst("yPos")) == 0))));
			put("BoxDB/3", new Policy("3", map -> ((Integer.parseInt(map.getFirst("xPos")) == 2)&&(Integer.parseInt(map.getFirst("yPos")) == 0))));
			put("BoxDB/4", new Policy("4", map -> ((Integer.parseInt(map.getFirst("xPos")) == 2)&&(Integer.parseInt(map.getFirst("yPos")) == 0))));
		}});
	}};

	private HashMap<String, HashMap<String, Condition>> entityPolicyMap = new HashMap<String, HashMap<String, Condition>>() {{
		put("Person", new HashMap<String, Condition>(){{
		}});
		put("Sample", new HashMap<String, Condition>(){{
			put("Freezer/insert", (map) -> (2 == 2));
			put("Freezer/retrieve", (map) -> (informationService.getSample(map.getFirst("SampleID")).owner.firstName.equals("niels")));
		}});
		put("Pizza", new HashMap<String, Condition>(){{
		}});
	}};

	public PolicyHandler(){}
	public PolicyHandler(HashMap<String, HashMap<String, Condition>> rolePolicyMap, HashMap<String, HashMap<String, Condition>> entityPolicyMap, InformationServiceInterface informationService){
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

	public boolean entityAuthorize(String Entity, String Action, MultivaluedMap<String, String> map) {
//		System.out.println("PolicyHandler.entityAuthorize()");
		try {
//			System.out.println(map.getFirst("ID"));
			return entityPolicyMap.get(Entity).get(Action).evaluate(map);
		} catch (Exception e) {
			System.out.println("\t " + Entity + " is not allowed to perform action: " + Action);
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

	// Class to handler printing condition
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
		for (String p : this.GetAllPoliciesForRole("Doctor")) {
			System.out.println(p);
		}
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
}
