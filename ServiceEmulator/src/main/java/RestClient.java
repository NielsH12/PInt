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

    public static String getBoxInfoByID(String BoxID, String UserID){
        Response response = sendGetBoxInfoByIDv2(BoxID, UserID);

        if(response.getStatus() != 200){
            throw new WebApplicationException(response.getStatus());
        }

        String readEntity = response.readEntity(String.class);

        dbBox Box = gson.fromJson(readEntity, dbBox.class);

        return Box.id + " " + Box.firstName+ " " + Box.lastName+ " " + Box.email+ " " + Box.expiration+ " " + Box.posX+ " " + Box.posY;
    }

    public static String retrieveBoxByID(String BoxID, String UserID){
        Response response = sendGetBoxInfoByIDv2(BoxID, UserID); // GET

        if(response.getStatus() != 200){
            return "Box is not in system" + response.getStatus();
        }

        dbBox t = gson.fromJson(response.readEntity(String.class), dbBox.class);

        if(t.posX == null || t.posY == null){
            return "Box is out of the freezer";
        }

        Response retrieveAutoResponse = sendRetrieveBoxFromAuto(BoxID, UserID);

        if(retrieveAutoResponse.getStatus() != 200){
            return "Freezer can't retrieve box" + retrieveAutoResponse.getStatus();
        }

        Response retrieveDDResponse = sendRetrieveBoxFromBoxDB(BoxID, UserID);

        if(retrieveDDResponse.getStatus() != 200){
            return "Database can't save box" + retrieveDDResponse.getStatus();
        }

        return "Success";
    }

    public static String insertBoxByID(String BoxID, String UserID){
        Response response = sendGetBoxInfoByIDv2(BoxID, UserID); // GET

        if(response.getStatus() != 200){
            return "Box is not in system" + response.getStatus();
        }

        String fisk = response.readEntity(String.class);

        dbBox Box = gson.fromJson(fisk, dbBox.class);

        if(Box.posX != null || Box.posY != null){
            return "Box is already in Freezer";
        }

        Response availableSlot = sendFindEmptySlot(UserID); // GET

        String s = availableSlot.readEntity(String.class);

        int[] integer = gson.fromJson(s, int[].class);

        if(integer[0] == -1 || integer[1] == -1){
            return "The freezer is full and can't take anymore boxes";
        }

        Response insertResult = sendInsertBoxAuto(UserID, BoxID, integer[0], integer[1]);

        if(insertResult.getStatus() != 200){
            return "Automation can't insert box " + insertResult.getStatus();
        }

        String s1 = insertResult.readEntity(String.class);

        if(!s1.equals("Success")){
            return "Something went really wrong";
        }

        Response insertAutoResult = sendInsertBoxDB(UserID, BoxID, integer[0], integer[1]);

        if(insertAutoResult.getStatus() != 200){
            return "Database had an error " + insertAutoResult.getStatus();
        }

        return "Success";
    }

    private static Response sendRetrieveBoxFromBoxDB(String BoxID, String userID){
        return client.target(REST_URI_Intermediator + "BoxDB/retrieve?ID=" + BoxID + "&UserID=" + userID).request().get();
    }

    private static Response sendRetrieveBoxFromAuto(String BoxID, String userID){
        return client.target(REST_URI_Intermediator + "Freezer/retrieve?ID=" + BoxID + "&UserID=" + userID).request().get();
    }

    private static Response sendGetBoxInfoByIDv2(String BoxID, String userID){
        return client.target(REST_URI_Intermediator + "BoxDB/get?ID=" + BoxID + "&UserID=" + userID).request().get();
    }

    private static Response sendFindEmptySlot(String userID){
        return client.target(REST_URI_Intermediator + "BoxDB/findEmptySlot?UserID=" + userID).request().get();
    }

    private static Response sendInsertBoxDB(String userID, String BoxID, int xPos, int yPos){
        return client.target(REST_URI_Intermediator + "BoxDB/insert?UserID=" + userID + "&ID=" + BoxID + "&xPos=" + xPos + "&yPos=" + yPos).request().get(); // GET
    }

    private static Response sendInsertBoxAuto(String userID, String BoxID, int xPos, int yPos){
        return client.target(REST_URI_Intermediator + "Freezer/insert?UserID=" + userID + "&ID=" + BoxID + "&xPos=" + xPos + "&yPos=" + yPos).request().get(); // GET
    }

    public static void main(String[] args){
        System.out.println(test().readEntity(String.class));
    }
}

// https://www.baeldung.com/jersey-jax-rs-client