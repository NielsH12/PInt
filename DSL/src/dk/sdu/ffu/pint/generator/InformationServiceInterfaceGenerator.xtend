package dk.sdu.ffu.pint.generator


import org.eclipse.emf.ecore.resource.Resource
import dk.sdu.ffu.pint.pint.Intermediary
import dk.sdu.ffu.pint.pint.Entity
import dk.sdu.ffu.pint.pint.tString
import dk.sdu.ffu.pint.pint.tInt
import dk.sdu.ffu.pint.pint.tFloat
import dk.sdu.ffu.pint.pint.tBoolean
import dk.sdu.ffu.pint.pint.tDate

// «»
class InformationServiceInterfaceGenerator{
	
	def static generate(Intermediary intermediary, Resource resource){
		'''
		package dk.nielshvid.intermediary;
		
		import javax.ws.rs.core.MultivaluedMap;
		
		public interface InformationServiceInterface {
		
		    String getRoleByEntity(String UserID, String EntityID);
		    String getRoleByOrganization(String UserID, String OrganizationID);
		    String getEntityType(MultivaluedMap<String, String> QPmap);
		    
		    «FOR _entity: intermediary.elements.filter(Entity)»
		    	Entities.«_entity.name» get«_entity.name»(«_entity.idType.generateType» id);
		    «ENDFOR»
		}
		'''
	}
	
	def static dispatch CharSequence generateType (tString x) '''String'''
	def static dispatch CharSequence generateType (tInt x) '''int'''
	def static dispatch CharSequence generateType (tFloat x) '''float'''
	def static dispatch CharSequence generateType (tBoolean x) '''boolean'''
	def static dispatch CharSequence generateType (tDate x) '''LocalDate'''
}
