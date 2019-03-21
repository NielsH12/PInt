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

    public static String getBoxInfoByID(String UserID, String BoxID){
        Response response = sendBoxDBGet(UserID, BoxID);

        String fisk = response.getHeaderString("Capability");

        System.out.println(fisk);

        if(response.getStatus() != 200){
            throw new WebApplicationException(response.getStatus());
        }

        String readEntity = response.readEntity(String.class);

        dbBox Box = gson.fromJson(readEntity, dbBox.class);

        return Box.id + " " + Box.firstName+ " " + Box.lastName+ " " + Box.email+ " " + Box.expiration+ " " + Box.posX+ " " + Box.posY;
    }

    public static String retrieveBoxByID(String BoxID, String UserID){
        Response response = sendBoxDBGet(UserID, BoxID); // GET

        if(response.getStatus() != 200){
            return "Box is not in system" + response.getStatus();
        }

        dbBox t = gson.fromJson(response.readEntity(String.class), dbBox.class);

        if(t.posX == null || t.posY == null){
            return "Box is out of the freezer";
        }

        UUID capability = UUID.fromString(response.getHeaderString("Capability"));

        Response retrieveAutoResponse = sendFreezerRetrieve(UserID, capability, BoxID, t.posX, t.posY);

        if(retrieveAutoResponse.getStatus() != 200){
            return "Freezer can't retrieve box" + retrieveAutoResponse.getStatus();
        }

        Response retrieveDDResponse = sendBoxDBRetrieve(UserID, capability, BoxID);

        if(retrieveDDResponse.getStatus() != 200){
            return "Database can't save box" + retrieveDDResponse.getStatus();
        }

        return "Success";
    }

    public static String insertBoxByID(String UserID, String BoxID){
        Response response = sendBoxDBGet(UserID, BoxID); // GET

        if(response.getStatus() != 200){
            return "Box is not in system" + response.getStatus();
        }

        String entity = response.readEntity(String.class);

        dbBox Box = gson.fromJson(entity, dbBox.class);

        if(Box.posX != null || Box.posY != null){
            return "Box is already in Freezer";
        }

        Response availableSlot = sendFindEmptySlot(UserID, Box.id); // GET

        String s = availableSlot.readEntity(String.class);

        int[] integer = gson.fromJson(s, int[].class);

        if(integer[0] == -1 || integer[1] == -1){
            return "The freezer is full and can't take anymore boxes";
        }

        UUID Capability = UUID.fromString(availableSlot.getHeaderString("Capability"));

        Response insertResult = sendFreezerInsert(UserID, Capability, BoxID, integer[0], integer[1]);

        if(insertResult.getStatus() != 200){
            return "Automation can't insert box " + insertResult.getStatus();
        }

        String s1 = insertResult.readEntity(String.class);

        if(!s1.equals("Success")){
            return "Something went really wrong";
        }

        Response insertAutoResult = sendBoxDBInsert(UserID, Capability, BoxID, integer[0], integer[1]);

        if(insertAutoResult.getStatus() != 200){
            return "Database had an error " + insertAutoResult.getStatus();
        }

        return "Success";
    }

    private static Response sendBoxDBRetrieve(String userID, UUID Capability, String BoxID){
        return client.target(REST_URI_Intermediator + "BoxDB/retrieve?UserID=" + userID + "&Capability=" + Capability + "&BoxID=" + BoxID).request().get();
    }

    private static Response sendFreezerRetrieve(String userID, UUID Capability, String BoxID, int xPos, int yPos){
        return client.target(REST_URI_Intermediator + "Freezer/retrieve?UserID=" + userID + "&Capability=" + Capability + "&BoxID=" + BoxID + "&xPos=" + xPos + "&yPos=" + yPos).request().get();
    }

    private static Response sendBoxDBGet(String userID, String BoxID){
        return client.target(REST_URI_Intermediator + "BoxDB/get?UserID=" + userID + "&BoxID=" + BoxID).request().get();
    }

    private static Response sendFindEmptySlot(String userID, String BoxID){
        return client.target(REST_URI_Intermediator + "BoxDB/findEmptySlot?UserID=" + userID + "&BoxID=" + BoxID).request().get();
    }

    private static Response sendBoxDBInsert(String userID, UUID Capability, String BoxID, int xPos, int yPos){
        return client.target(REST_URI_Intermediator + "BoxDB/insert?UserID=" + userID + "&Capability=" + Capability + "&BoxID=" + BoxID + "&xPos=" + xPos + "&yPos=" + yPos).request().get(); // GET
    }

    private static Response sendFreezerInsert(String userID, UUID Capability, String BoxID, int xPos, int yPos){
        return client.target(REST_URI_Intermediator + "Freezer/insert?UserID=" + userID + "&Capability=" + Capability + "&BoxID=" + BoxID + "&xPos=" + xPos + "&yPos=" + yPos).request().get(); // GET
    }

    public static void main(String[] args){
        System.out.println(test().readEntity(String.class));
    }
}

// https://www.baeldung.com/jersey-jax-rs-client