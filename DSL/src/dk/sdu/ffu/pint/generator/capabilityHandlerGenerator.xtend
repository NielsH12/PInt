package dk.sdu.ffu.pint.generator


import org.eclipse.emf.ecore.resource.Resource
import dk.sdu.ffu.pint.pint.Intermediary
import dk.sdu.ffu.pint.pint.Capability
import dk.sdu.ffu.pint.pint.Endpoint
import dk.sdu.ffu.pint.pint.RestResource
import dk.sdu.ffu.pint.pint.CapabilityRestResource

// «»

class capabilityHandlerGenerator{
	
	def static generate(Intermediary intermediary, Resource resource){
		'''
		package dk.nielshvid.intermediary;
		import java.time.LocalTime;
		import java.util.ArrayList;
		import java.util.HashMap;
		import java.util.List;
		import java.util.UUID;
		
		public class CapabilityHandler {
		    private static HashMap<UUID, Capability> capabilities = new HashMap<>();
		    private static LocalTime lastClean = LocalTime.now();
		    private static final int CAPABILITY_LIFETIME_SECONDS = 300;
		    private HashMap<String, Node<String>> capabilityTemplates = new HashMap<String, Node<String>>(){{
			«val _capability = intermediary.elements.filter(Capability)»
			«IF(_capability !== null && _capability.size > 0)»
				«FOR _capabilities: _capability.get(0).capabilities»
				put("«findResource(_capabilities.restResource)»/«_capabilities.restResource.name»", new Node<String>("«findResource(_capabilities.restResource)»/«_capabilities.restResource.name»"){{
					«FOR _subRestResource: _capabilities.subRestResource»
						«addChild(_subRestResource)»
					«ENDFOR»
				}});
				«ENDFOR»
			«ENDIF»
		    	

		        
		    }}; 
		    public CapabilityHandler(){}
		
		    public CapabilityHandler(HashMap<String, Node<String>> capabilityTemplates){
		        this.capabilityTemplates = capabilityTemplates;
		    }

			public UUID addCapability(String userID, String key){
			
				if(!capabilityTemplates.containsKey(key)){
					return null;
				}
				
				Node<String> capabilityTemplate = capabilityTemplates.get(key);
				
				Capability capability = new Capability(userID, capabilityTemplate);
				capabilities.put(capability.getID(), capability);
				return capability.ID;
			}
			
			public boolean authorize(String UserID, UUID CapabilityID, String resource){
				if(lastClean.plusHours(24).isBefore(LocalTime.now())){
					cleanCapabilities();
				}
			
				if(!capabilities.containsKey(CapabilityID)){
					return false;
				}
			
				boolean result =  capabilities.get(CapabilityID).useResource(UserID, resource);
				if(capabilities.get(CapabilityID).delete()){
					capabilities.remove(CapabilityID);
				}
				return result;
			}
			
			private void cleanCapabilities(){
		       lastClean = LocalTime.now();
		
				ArrayList<UUID> tempUUIDList = new ArrayList<>(capabilities.keySet());
				for (UUID i : tempUUIDList){
					if (capabilities.get(i).delete()){
						capabilities.remove(i);
					}
				}
			}
			
			private class Capability {
				private LocalTime lastUsed;
				private Node<String> Resources;
				private UUID ID;
				private String userID;
				
				Capability(String userID, Node<String> Resources){
					this.userID = userID;
					this.Resources = Resources;
					
					ID = UUID.randomUUID();
					lastUsed = LocalTime.now();
				}
				
				UUID getID() {
					return ID;
				}
				
				boolean delete(){
					LocalTime temp = LocalTime.now();
					if(!temp.isBefore(lastUsed.plusSeconds(CAPABILITY_LIFETIME_SECONDS))){ // debug value
						return true;
					}
					return false;
				}
				
				boolean useResource(String UserID, String resource){
					LocalTime temp = LocalTime.now();
					
					if(!UserID.equals(this.userID)){
						return false;
					}
					
					if(!temp.isBefore(lastUsed.plusSeconds(CAPABILITY_LIFETIME_SECONDS))){ // debug value
						return false;
					}
					
					Node<String> t = this.Resources.useResource(resource);
					if (t != null){
						this.Resources = t;
						return true;
					}
					return false;
				}
			}
			
			public static class Node<T> {
			
				private T data = null;
				private List<Node<T>> children = new ArrayList<>();
				
				protected Node(T data) {
					this.data = data;
				}
				
				protected Node<T> addChild(Node<T> child) {
					this.children.add(child);
					return child;
				}
				
				Node<T> useResource(T resource){
					for (Node n : this.getChildren()){
						if (n.data == resource){
							return n;
						}
					}
					return null;
				}
				
				private List<Node<T>> getChildren() {
					return children;
				}
			}
		}
		'''
	}
	
	def static dispatch CharSequence getResourceName(Endpoint ep){
		ep.name
	}
	
	def static dispatch CharSequence getResourceName(RestResource re){
	}
	
	def static CharSequence findResource(RestResource restResource){
		restResource.eContainer.getResourceName
	}
	
	def static CharSequence addChild(CapabilityRestResource capRestResource){
		'''
		addChild(new Node<String>("«findResource(capRestResource.restResource)»/«capRestResource.restResource.name»"){{
		«FOR _subRestResource: capRestResource.nestedCapabilityRestResources»
			«addChild(_subRestResource)»
		«ENDFOR»
		}});
		'''
	}
}
