package dk.nielshvid.intermediary;

import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.HashMap;


//TODO: comapre dates er ikke med i dennne klasse
public class Entities {
    public enum EntityType {
        SAMPLE,
        PERSON,
        PIZZA,
        ENTITY_TYPES_WITH_NOTHING_FOR_TESTING
    }

    public interface EntityInterface {
        boolean evaluateByActionKey(String actionKey);
    };

    public static abstract class Entity implements EntityInterface{
        public String ID;
    }

    public static class Person extends Entity {
        public String firstName;
        public String lastName;
        public String email;

        public boolean evaluate(PersonEvaluate entityEvaluate){
            return entityEvaluate.evaluate(this);
        }

        public boolean evaluateByActionKey(String actionKey){
            PersonEvaluate personEvaluate = entityPolicyMap.get(actionKey);
            if(personEvaluate != null){
                return personEvaluate.evaluate(this);
            }
            return false;
        }

        private final HashMap<String, PersonEvaluate> entityPolicyMap = new HashMap<String, PersonEvaluate>() {{
            put("Freezer/insert", (person) -> person.evaluate(sample1 -> person.firstName.equals("niels")));
            put("Freezer/retrieve", (person) -> person.evaluate(sample1 -> person.email.equals("hej@hej.dk")));
        }};
    }
    public static class Sample extends Entity {
        public LocalDate created;
        public LocalDate accessed;
        public LocalDate expiration;
        public Person owner;
        public int temperature;

        public boolean evaluate(SampleEvaluate sampleEvaluate){
            return sampleEvaluate.evaluate(this);
        }

        public boolean evaluateByActionKey(String actionKey){
            SampleEvaluate sampleEvaluate = entityPolicyMap.get(actionKey);
            if(sampleEvaluate != null){
                return sampleEvaluate.evaluate(this);
            }
            return false;
        }

        //TODO: det her er mit bedste bud ind til videre
//        public MultivaluedMap<String, String> returnAsMap(){
//            MultivaluedMap<String, String> multivaluedMap = new MultivaluedHashMap<>();
//            multivaluedMap.add("created", this.created.toString());
//            multivaluedMap.add("accessed", this.accessed.toString());
//            multivaluedMap.add("expiration", this.expiration.toString());
//            multivaluedMap.add("temperature", Integer.toString(this.temperature));
////            this.owner.returnAsMap().forEach((String key, List<String> list)-> { ... });
//            return multivaluedMap;
////        }

        private final HashMap<String, SampleEvaluate> entityPolicyMap = new HashMap<String, SampleEvaluate>() {{
            put("Freezer/insert", (sample) -> sample.evaluate(sample1 -> sample1.temperature == 1));
			put("Freezer/retrieve", (sample) -> sample.evaluate(sample1 -> sample1.owner.firstName.equals("niels")));
        }};
    }

    public static class Pizza extends Entity {
        public LocalDate expirationDate;
        public String ingredients;
        public boolean gluten;
        public int baketime;

        public boolean evaluate(PizzaEvaluate pizzaEvaluate){
            return pizzaEvaluate.evaluate(this);
        }

        public boolean evaluateByActionKey(String actionKey){
            PizzaEvaluate pizzaEvaluate = entityPolicyMap.get(actionKey);
            if(pizzaEvaluate != null){
                return pizzaEvaluate.evaluate(this);
            }
            return false;
        }

        private final HashMap<String, PizzaEvaluate> entityPolicyMap = new HashMap<String, PizzaEvaluate>() {{
            put("Freezer/insert", (pizza) -> pizza.evaluate(sample1 -> pizza.baketime == 2));
            put("Freezer/retrieve", (pizza) -> pizza.evaluate(sample1 -> pizza.gluten == false));
        }};
    }

    public interface SampleEvaluate {
        boolean evaluate(Sample sample );
    }
    public interface PersonEvaluate {
        boolean evaluate(Person person);
    }
    public interface PizzaEvaluate {
        boolean evaluate(Pizza pizza);
    }

    @Test
    public void Jensadasdadsd(){
        Sample sample = new Sample();
        sample.temperature = 1;
        sample.created = LocalDate.now();
        sample.expiration = LocalDate.now();
        sample.accessed = LocalDate.now();

        System.out.println(sample.evaluateByActionKey("Freezer/insert"));
    }
}
