package dk.nielshvid.intermediary;

import com.google.gson.Gson;
import dk.nielshvid.intermediary.Entities.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

public class EntityService {

    private static Client client = ClientBuilder.newClient();
    private static Gson gson = new Gson();
    private static int checkCounter;
	private static Person person;
	private static Sample sample;
	private static Pizza pizza;

	
	public static Person getPerson(String id, int counter){
		if(checkCounter == counter){
			return person;
		}
		
		Response response = client.target("127.0.0.1/" + "getPerson?PersonID=" + id).request().get();
		
		String readEntity = response.readEntity(String.class);
		
		person = gson.fromJson(readEntity, Person.class);
		checkCounter = counter;
		return person;
	}
	
	public static Sample getSample(String id, int counter){
		if(checkCounter == counter){
			return sample;
		}
		
		Response response = client.target("127.0.0.1/" + "getSample?SampleID=" + id).request().get();
		
		String readEntity = response.readEntity(String.class);
		
		sample = gson.fromJson(readEntity, Sample.class);
		checkCounter = counter;
		return sample;
	}
	
	public static Pizza getPizza(String id, int counter){
		if(checkCounter == counter){
			return pizza;
		}
		
		Response response = client.target("127.0.0.1/" + "getPizza?PizzaID=" + id).request().get();
		
		String readEntity = response.readEntity(String.class);
		
		pizza = gson.fromJson(readEntity, Pizza.class);
		checkCounter = counter;
		return pizza;
	}
}
