import com.google.gson.Gson;
import dk.nielshvid.storagemanagement.dbBox;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

public class RestClient {

    private static final String REST_URI_Intermediator = "http://localhost:8081/";
    private static Client client = ClientBuilder.newClient();
    private static Gson gson = new Gson();


    public static Response test(){
        return client.target(REST_URI_Intermediator).request(MediaType.TEXT_PLAIN).get();
    }

    public static Response getBoxInfoByID(String ID){
        return client.target(REST_URI_Intermediator + "get?ID=" + ID).request().get(); // GET
    }

    public static Response retrieveBoxByID(String ID){
        return client.target(REST_URI_Intermediator + "retrieve?ID=" + ID).request().get(); // OPEN
    }

    public static Response insertBoxByID(String ID){
        return client.target(REST_URI_Intermediator + "insert?ID=" + ID).request().get(); // INSERT
    }

    public static Response getBoxInfoByIDv2(String BoxID, UUID userID){
        return client.target(REST_URI_Intermediator + "getBoxInfoByID?ID=" + BoxID + "&UserID=" + userID).request().get(); // GET
    }

    public static Response retrieveBoxByIDv2(String ID){


        return client.target(REST_URI_Intermediator + "retrieve?ID=" + ID).request().get(); // OPEN
    }

    public static String insertBoxByIDv2(String ID){
        Response response = client.target(REST_URI_Intermediator + "getBoxInfoByID?ID=" + ID).request().get(); // GET

        if(response.getStatus() != 200){
            throw new WebApplicationException(Response.status(500).entity("Box is not in system").type("text/plain").build());
        }

        String fisk = response.readEntity(String.class);

        dbBox Box = gson.fromJson(fisk, dbBox.class);

        if(Box.posX != null || Box.posY != null){
            return "Box is already in Freezer";
        }

        Response availableSlot = client.target(REST_URI_Intermediator + "findEmptySlot").request().get(); // GET

        String s = availableSlot.readEntity(String.class);

        int[] integer = gson.fromJson(s, int[].class);

        if(integer[0] == -1 || integer[1] == -1){
            return "The freezer is full and can't take anymore boxes";
        }

        Response insertResult = client.target(REST_URI_Intermediator + "insertBox?ID="+ ID + "posX=" + integer[0] + "posY=" + integer[1]).request().get(); // GET

        String s1 = insertResult.readEntity(String.class);

        if(!s1.equals("1")){
            return "Something went really wring";
        }

        Response insertAutoResult = client.target(REST_URI_Intermediator + "insertAuto?ID="+ ID + "posX=" + integer[0] + "posY=" + integer[1]).request().get(); // GET

        if(insertAutoResult.getStatus() != 200){
            throw new WebApplicationException(Response.status(500).entity("Automation had a error").type("text/plain").build());
        }

        return "Success";
    }


    public static void main(String[] args){
        System.out.println(test().readEntity(String.class));
    }
}

// https://www.baeldung.com/jersey-jax-rs-client