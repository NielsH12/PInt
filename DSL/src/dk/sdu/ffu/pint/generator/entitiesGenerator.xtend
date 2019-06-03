package dk.sdu.ffu.pint.generator

import org.eclipse.emf.ecore.resource.Resource
import dk.sdu.ffu.pint.pint.Intermediary
import dk.sdu.ffu.pint.pint.tString
import dk.sdu.ffu.pint.pint.tInt
import dk.sdu.ffu.pint.pint.tFloat
import dk.sdu.ffu.pint.pint.tBoolean
import dk.sdu.ffu.pint.pint.tDate
import dk.sdu.ffu.pint.pint.EntityRef
import dk.sdu.ffu.pint.pint.Entity

// «»

class entitiesGenerator{
	
	def static generate(Intermediary intermediary, Resource resource){
		'''
		package dk.nielshvid.intermediary;
		
		import java.time.LocalDate;
		public class Entities {
			«FOR entity: intermediary.elements.filter(Entity)»
			public static class «entity.name» {
				«FOR property: entity.properties»
				public «property.type.generateType» «property.name»;
				«ENDFOR»
			}
			«ENDFOR»
		}
		'''
	}
	
	def static dispatch CharSequence generateType (tString x) '''String'''
	def static dispatch CharSequence generateType (tInt x) '''int'''
	def static dispatch CharSequence generateType (tFloat x) '''float'''
	def static dispatch CharSequence generateType (tBoolean x) '''boolean'''
	def static dispatch CharSequence generateType (tDate x) '''LocalDate'''
	def static dispatch CharSequence generateType (EntityRef x) '''«x.ref.name»'''
}