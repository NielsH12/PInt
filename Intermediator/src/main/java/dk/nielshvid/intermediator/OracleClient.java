package dk.nielshvid.intermediator;

import com.google.gson.Gson;
import dk.nielshvid.intermediator.Entities.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

public class OracleClient {
    private static final String REST_URI_Freezer = "http://localhost:8082/";

    private static Client client = ClientBuilder.newClient();
    private static Gson gson = new Gson();
    private static int checkCounter; // counteren går at man ikke behøver at hente et nyt object hver man stadig er inde for den samme policy
    private static Sample sample;
    private static Person person;

    public static Sample getSample(String id, int counter){

        if(checkCounter == counter){
            return sample;
        }
        Response response = client.target(REST_URI_Freezer + "getSample?ID=" + id).request().get();

        String readEntity = response.readEntity(String.class);

        sample = gson.fromJson(readEntity, Sample.class);
        checkCounter = counter;
        return sample;
    }

    public static Person getPerson(String id, int counter){

        if(checkCounter == counter){
            return person;
        }
        Response response = client.target(REST_URI_Freezer + "getPerson?ID=" + id).request().get();

        String readEntity = response.readEntity(String.class);

        person = gson.fromJson(readEntity, Person.class);
        checkCounter = counter;
        return person;
    }
}
