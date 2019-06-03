package dk.sdu.ffu.pint.generator

import dk.sdu.ffu.pint.pint.Intermediary
import dk.sdu.ffu.pint.pint.QueryParam
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.resource.Resource
import java.util.HashSet
import dk.sdu.ffu.pint.pint.Capability
import dk.sdu.ffu.pint.pint.Endpoint
import dk.sdu.ffu.pint.pint.RestResource

// «»

class PintIntermediaryGenerator{
	
	def static generate(Intermediary intermediary, Resource resource){
		
		val HashSet<String> capabilityIssuingActions = new HashSet<String>();
		
		val _capability = intermediary.elements.filter(Capability);

		if (_capability !== null && _capability.size > 0){
			for (_capabilities: _capability.get(0).capabilities){
				println("inserting: " + findResource(_capabilities.restResource) + "/" + _capabilities.restResource.name)
				capabilityIssuingActions.add(findResource(_capabilities.restResource) + "/" + _capabilities.restResource.name)
			}
		}

		'''
		package dk.nielshvid.intermediary;
		
		import javax.ws.rs.*;
		import javax.ws.rs.core.*;
		import java.util.UUID;
		
		
		@Path("/")
		public class Pint{
			private Shield shield = new Shield();
		
			«FOR dEndpoint : intermediary.elements.filter(Endpoint)»
				«FOR dRestResources : dEndpoint.restResources»
					@Path("«dRestResources.path»«dRestResources.httpVerb.determinePathParam»")
					@«dRestResources.httpVerb»
					@Produces(MediaType.«mediaTypeMapper(dRestResources.product)»)
					public Response «dRestResources.name»«dEndpoint.name» (@Context UriInfo info, String body, @Context HttpHeaders headers«dRestResources.httpVerb.pathParamFixer»){
						
						«dRestResources.httpVerb.checkEntityIDMatch»
						«if(capabilityIssuingActions.contains(dEndpoint.name + "/" + dRestResources.name)){
						'''
						UUID CapabilityID = shield.generateCapability("«dEndpoint.name»/«dRestResources.name»", info.getQueryParameters(), body, «dRestResources.httpVerb.post»);
						'''
						}»
						
						// Check policies
						if (shield.«dRestResources.httpVerb.callAuthorize»("«dEndpoint.name»/«dRestResources.name»", info.getQueryParameters(), body)){
							// Forward request
							«if(capabilityIssuingActions.contains(dEndpoint.name + "/" + dRestResources.name)){
								'''return Response.fromResponse(IntermediaryClient.«dEndpoint.name»«dRestResources.httpVerb»«dRestResources.name»(info, body, headers)).header("Capability", CapabilityID).build();'''
							} else {
								'''return IntermediaryClient.«dEndpoint.name»«dRestResources.httpVerb»«dRestResources.name»(info, body, headers);'''
							}»
						};
						
						throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
						
					}
					
				«ENDFOR»
			«ENDFOR»
		}
		'''
	}
	
	def static CharSequence forwardArgumentMapper(EList<QueryParam> qpList){
		
		var returnVar = "UserID, EntityID"
		
		for (QueryParam qp: qpList){
			returnVar += ", " + qp.name
		}
		
		return returnVar;
	}
	
	def static CharSequence QueryParamMapper(EList<QueryParam> qpList){
		var returnVar = '''@Context UriInfo info, @QueryParam("UserID") String UserID, @QueryParam("Capability") UUID Capability, @QueryParam("EntityID") String EntityID'''
		
		for (QueryParam qp: qpList){
			returnVar += ", @QueryParam(\"" + qp.name + "\") " + paramTypeMapper(qp.type) + " " + qp.name 
		}
		
		return returnVar;
	}
	
	def static CharSequence paramTypeMapper(String type){
		switch(type){
			
			case "int":
				return "int"
				
			case "string":
				return "String"
				
			default:
				throw new Error("Parameter type: " + type + " unknown")
		}
	}
	
	def static CharSequence mediaTypeMapper(String pt){
		switch(pt){
			case "plain":
				return "TEXT_PLAIN"

			case "json":
				return "APPLICATION_JSON"
				
			case "html":
				return "TEXT_HTML"
			
			default:
				throw new Error("Produce type: " + pt + " unknown")
		}
	}
	def static dispatch CharSequence getResourceName(Endpoint re){
		re.name
	}
	def static dispatch CharSequence getResourceName(RestResource re){
	}
	
	def static CharSequence findResource(RestResource action){
		action.eContainer.getResourceName
	}
	
	def static CharSequence callAuthorize(String x){
		if (x.equals("POST")){
			return '''postAuthorize'''
		} else {
			return '''authorize'''
		}
	}
	
	def static CharSequence determinePathParam(String x){
		if (x.equals("POST")){
			return ''''''
		} else {
			return '''/{EntityID}'''
		}
	}
	
	def static CharSequence checkEntityIDMatch(String x){
		if (x.equals("POST")){
			return ''''''
		} else {
			return 
			'''
			if(!entityID.equals(String.valueOf(info.getQueryParameters().getFirst("EntityID")))){
				throw new WebApplicationException(Response.Status.BAD_REQUEST);
			}
			'''
		}
	}
	
	def static CharSequence post(String x){
		if (x.equals("POST")){
			return '''true'''
		} else {
			return 
			'''false'''
		}
	}	
	def static CharSequence pathParamFixer(String x){
		if (x.equals("POST")){
			return ''''''
		} else {
			return 
			''', @PathParam("EntityID") String entityID'''
		}
	}
}
