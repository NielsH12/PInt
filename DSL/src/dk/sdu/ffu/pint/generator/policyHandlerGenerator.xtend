package dk.sdu.ffu.pint.generator

import dk.sdu.ffu.pint.pint.AND
import dk.sdu.ffu.pint.pint.Add
import dk.sdu.ffu.pint.pint.Bool
import dk.sdu.ffu.pint.pint.Comparison
import dk.sdu.ffu.pint.pint.DateComparison
import dk.sdu.ffu.pint.pint.Div
import dk.sdu.ffu.pint.pint.Intermediary
import dk.sdu.ffu.pint.pint.EntityPolicy
import dk.sdu.ffu.pint.pint.EntityProperty
import dk.sdu.ffu.pint.pint.EntityPropertyRef
import dk.sdu.ffu.pint.pint.IntExp
import dk.sdu.ffu.pint.pint.Mul
import dk.sdu.ffu.pint.pint.OR
import dk.sdu.ffu.pint.pint.QueryParamRef
import dk.sdu.ffu.pint.pint.RelEQ
import dk.sdu.ffu.pint.pint.RelGT
import dk.sdu.ffu.pint.pint.RelGTE
import dk.sdu.ffu.pint.pint.RelLT
import dk.sdu.ffu.pint.pint.RelLTE
import dk.sdu.ffu.pint.pint.RelNEQ
import dk.sdu.ffu.pint.pint.RelationalOp
import dk.sdu.ffu.pint.pint.RolePolicy
import dk.sdu.ffu.pint.pint.RoleRef
import dk.sdu.ffu.pint.pint.StringPrim
import dk.sdu.ffu.pint.pint.Sub
import dk.sdu.ffu.pint.pint.rComparison
import dk.sdu.ffu.pint.pint.today
import org.eclipse.emf.ecore.resource.Resource
import dk.sdu.ffu.pint.pint.StringComparison
import dk.sdu.ffu.pint.pint.rStringComparison
import dk.sdu.ffu.pint.pint.Role
import dk.sdu.ffu.pint.pint.Entity
import dk.sdu.ffu.pint.pint.Endpoint
import dk.sdu.ffu.pint.pint.RestResource
import dk.sdu.ffu.pint.pint.BodyRef
import dk.sdu.ffu.pint.pint.LeafElement
import dk.sdu.ffu.pint.pint.BranchElement
import org.eclipse.xtext.EcoreUtil2

// «»
class policyHandlerGenerator{
	def static generate(Intermediary intermediary, Resource resource){
		
	'''
	package dk.nielshvid.intermediary;
	
	import org.json.JSONObject;
	import javax.ws.rs.core.MultivaluedMap;
	import java.time.LocalDate;
	import java.util.HashMap;
	import java.time.temporal.ChronoUnit;
	import org.json.JSONException;
	
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
			«FOR _role: intermediary.elements.filter(Role)»
				put("«_role.name»", new HashMap<String, RoleCondition>(){{ 
					«FOR rPolicy: intermediary.elements.filter(RolePolicy).filter[role.roleRefs instanceof RoleRef].filter[role.roleRefs.ref.name.equals(_role.name)]»
						put("«findResource(rPolicy.restResource)»/«rPolicy.restResource.name»", (map, body) -> «printCondition(rPolicy)»);
					«ENDFOR»
				}});
			«ENDFOR»
		}};
		
		private HashMap<String, HashMap<String, EntityCondition>> entityPolicyMap = new HashMap<String, HashMap<String, EntityCondition>>() {{
			«FOR _entity: intermediary.elements.filter(Entity)»
				put("«_entity.name»", new HashMap<String,EntityCondition>(){{
				«FOR _endpoint: intermediary.elements.filter(Endpoint)»
					«FOR _restResources: _endpoint.restResources»
					«var ePolicy = intermediary.elements.filter(EntityPolicy).filter[_entity.name.equals(entity.name)].findFirst[(findResource(restResource) + "/" +restResource.name).equals(_endpoint.name + "/" + _restResources.name)]»
						«IF (ePolicy !== null)»
							put("«_endpoint.name»/«_restResources.name»", (map) -> «printCondition(ePolicy)»);
						«ENDIF»
					«ENDFOR»
				«ENDFOR»
				}});
			«ENDFOR»
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
		
		private interface RoleCondition {
			boolean evaluate(MultivaluedMap<String, String> map, JSONObject body) throws JSONException;
		}
		private interface EntityCondition {
			boolean evaluate(MultivaluedMap<String, String> mMap);
		}
		
		public static int CompareDates(LocalDate from, LocalDate to){
			return (int) ChronoUnit.DAYS.between(from, to);
		}
	}
	'''
	}
	
	def static CharSequence printCondition(RolePolicy RP){
		var returnVar = ""
		if(RP !== null){
			if (RP.require === null){
				return "true"
			}
			returnVar = RP.require.requirement.rGenerateLogic.toString;
		} else {
			returnVar = "null"
		}
		
		return returnVar;
	}
	
	def static CharSequence printCondition(EntityPolicy EP){
		var returnVar = ""
		if(EP !== null){
			println("EP not null")
			returnVar = EP.require.requirement.generateLogic.toString;
		} else {
			returnVar = "null"
		}
		
		return returnVar;
	}
	
	
	def static Entity getEntity(EntityProperty entityProperty){
		val entityPolicy = EcoreUtil2.getContainerOfType(entityProperty, EntityPolicy)
        return entityPolicy.entity
	}
		
	def static dispatch CharSequence generateLogic(OR x) '''(«x.left.generateLogic»||«x.right.generateLogic»)'''
	def static dispatch CharSequence generateLogic(AND x) '''(«x.left.generateLogic»&&«x.right.generateLogic»)'''
	def static dispatch CharSequence generateLogic(Comparison x) '''(«x.left.generateExp» «x.op.generateOp» «x.right.generateExp»)'''
	def static dispatch CharSequence generateLogic(Bool x) '''«x.bool»'''
	def static dispatch CharSequence generateLogic(StringPrim x) '''("«x.value»")'''
	def static dispatch CharSequence generateExp(DateComparison x) '''CompareDates(«x.left.generateExp», «x.right.generateExp»)'''
	def static dispatch CharSequence generateLogic(StringComparison x) '''(«x.left.generateExp».equals(«x.right.generateExp»))'''
	def static dispatch CharSequence generateExp(Add x) '''(«x.left.generateExp»+«x.right.generateExp»)'''
	def static dispatch CharSequence generateExp(Sub x) '''(«x.left.generateExp»-«x.right.generateExp»)'''
	def static dispatch CharSequence generateExp(Mul x) '''(«x.left.generateExp»*«x.right.generateExp»)'''
	def static dispatch CharSequence generateExp(Div x) '''(«x.left.generateExp»/«x.right.generateExp»)'''
	def static dispatch CharSequence generateExp(IntExp x) '''«x.value»'''
	def static dispatch CharSequence generateExp(EntityProperty x) {
		return '''informationService.get«x.getEntity.name»(map.getFirst("«x.getEntity.idName»"))«findProperty(x.entityPropertyRef)»'''
	}
	def static dispatch CharSequence generateExp(today x) '''LocalDate.now()'''
	def static dispatch CharSequence generateExp(StringPrim x) '''"«x.value»"'''
	
	def static generateOp(RelationalOp op) {
		switch op {	RelEQ: '==' RelLT: '<' RelGT: '>' RelLTE: '<=' RelGTE: '>=' RelNEQ: '!=' }
	}

	def static dispatch CharSequence getResourceName(Endpoint ep){
		ep.name
	}
	def static dispatch CharSequence getResourceName(RestResource re){
	}
	
	def static CharSequence findResource(RestResource restResource){
		restResource.eContainer.getResourceName
	}	
	
	def static CharSequence findProperty(EntityPropertyRef entityPropertyRef){
		if(entityPropertyRef === null) return ""
		return "."+ entityPropertyRef.propertyRef.name + findProperty(entityPropertyRef.ref)
	}
	
//	Role logic
	
	def static dispatch CharSequence rGenerateLogic(OR x) '''(«x.left.rGenerateLogic»||«x.right.rGenerateLogic»)'''
	def static dispatch CharSequence rGenerateLogic(AND x) '''(«x.left.rGenerateLogic»&&«x.right.rGenerateLogic»)'''
	def static dispatch CharSequence rGenerateLogic(rComparison x) '''(«x.left.rGenerateExp» «if(x.op !== null) {x.op.generateOp}» «if(x.right !== null) {x.right.rGenerateExp}»)'''
	def static dispatch CharSequence rGenerateLogic(rStringComparison x) '''(«x.left.rGenerateExp».equals(«x.right.rGenerateExp»))'''
	def static dispatch CharSequence rGenerateExp(Add x) '''(«x.left.rGenerateExp»+«x.right.rGenerateExp»)'''
	def static dispatch CharSequence rGenerateExp(Sub x) '''(«x.left.rGenerateExp»-«x.right.rGenerateExp»)'''
	def static dispatch CharSequence rGenerateExp(Mul x) '''(«x.left.rGenerateExp»*«x.right.rGenerateExp»)'''
	def static dispatch CharSequence rGenerateExp(Div x) '''(«x.left.rGenerateExp»/«x.right.rGenerateExp»)'''
	def static dispatch CharSequence rGenerateExp(IntExp x) '''«x.value»'''
	def static dispatch CharSequence rGenerateExp(Bool x) '''«x.bool»'''
	def static dispatch CharSequence rGenerateExp(StringPrim x) '''"«x.value»"'''
	def static dispatch CharSequence rGenerateExp(QueryParamRef x){
		switch(x.ref.type){
			case "string": '''map.getFirst("«x.ref.name»")'''
			case "int": '''Integer.parseInt(map.getFirst("«x.ref.name»"))'''
			default: {println("reftype: " + x.ref.type); return '''NOT IMPLEMENTED'''}
		}	
	}
	def static dispatch CharSequence rGenerateExp(BodyRef x) '''«x.jsonRef.rGetJsonType»body«x.jsonRef.rGenerateExp» )'''

	
	def static dispatch CharSequence rGenerateExp(BranchElement x){
		'''.getJSONObject("«x.jsonObjectRef.name»")«x.child.rGenerateExp»'''
	}
	
	def static dispatch CharSequence rGenerateExp(LeafElement x){
		switch(x.kvPairRef.type){
			case "string": '''.getString("«x.kvPairRef.name»")'''
			case "int": '''.getInt("«x.kvPairRef.name»")'''
			default: {println("reftype: " + x.kvPairRef.type); return '''NOT IMPLEMENTED'''}
		}
	}
	
	def static dispatch CharSequence rGetJsonType(LeafElement x){
		switch(x.kvPairRef.type){
			case "int": '''Integer.parseInt('''
			case "string": '''('''
			
			default: {println("reftype: " + x.kvPairRef.type); return '''NOT IMPLEMENTED'''}
		}
		
	}
		
	def static dispatch CharSequence rGetJsonType(BranchElement x){
			x.child.rGetJsonType
	}
	
	
	
	
	
	
	
	
}