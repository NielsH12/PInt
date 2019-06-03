package dk.sdu.ffu.pint.generator

import org.eclipse.emf.ecore.resource.Resource
import dk.sdu.ffu.pint.pint.Intermediary
import org.eclipse.emf.common.util.EList
import dk.sdu.ffu.pint.pint.QueryParam
import dk.sdu.ffu.pint.pint.Endpoint
import dk.sdu.ffu.pint.pint.RestResource


// «»

class IntermediaryClientGenerator{
	
	def static generate(Intermediary intermediary, Resource resource){
		'''
		package dk.nielshvid.intermediary;
		
		import javax.ws.rs.client.Client;
		import javax.ws.rs.client.ClientBuilder;
		import javax.ws.rs.core.Response;
		import javax.ws.rs.client.Entity;
		import javax.ws.rs.client.Invocation;
		import java.util.Map;
		import javax.ws.rs.core.UriInfo;
		import javax.ws.rs.core.HttpHeaders;
		import javax.ws.rs.core.Cookie;
		
		public class IntermediaryClient{
		
		«FOR endpoint : intermediary.elements.filter(Endpoint)»
			private static final String REST_URI_«endpoint.name» = "«endpoint.uri»";
		«ENDFOR»
		
		private static Client client = ClientBuilder.newClient();
		
		«FOR endpoint : intermediary.elements.filter(Endpoint)»
			«FOR restResources : endpoint.restResources»				
				public static Response «endpoint.name»«restResources.httpVerb»«restResources.name»(UriInfo info, String body, HttpHeaders headers){
					Invocation.Builder builder = client
						.target(REST_URI_«endpoint.name»)
						.path("«restResources.path»")
						.request()
						.header("Content-Type", "«restResources.ContentType»");
						
						for (Map.Entry<String, Cookie> entry : headers.getCookies().entrySet()){
							builder.cookie(entry.getValue());
						}
						
						«restResources.ResponseBuilder»
				}
				
			«ENDFOR»
		«ENDFOR»
		}
		'''
	}
	
	def static CharSequence ContentType(RestResource x){
		switch(x.product){
			case 'html': return '''text/html'''
			case 'plain': return '''text/plain'''
			case 'json': return '''application/json'''
			
			default: println(x.product + "not supported")
		}
	}
	
	def static CharSequence RestParamMapper(EList<QueryParam> qpList){
		var returnVar = '''?UserID=" + UserID + "&EntityID=" + EntityID'''
		
		for (qp : qpList){
			returnVar += ''' + "&«qp.name»=" + «qp.name»'''
		}
		
		return returnVar
	}
	
	def static CharSequence QueryParamMapper(EList<QueryParam> qpList){
		var returnVar = "String UserID, String EntityID";
		returnVar += ", MultivaluedMap<String, String> qmap"
		
		return returnVar;
	}
	
	def static CharSequence ResponseBuilder(RestResource x){
		switch(x.httpVerb){
			case 'GET': return '''return builder.get();'''
			case 'DELETE': return '''return builder.delete();'''
			case 'POST': return '''return builder.post(Entity.json(body));'''
			case 'PUT': return '''return builder.put(Entity.json(body));'''
			
			default: println(x.httpVerb + "not supported")
		}
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
}