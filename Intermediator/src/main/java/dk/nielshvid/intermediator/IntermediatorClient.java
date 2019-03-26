package dk.nielshvid.intermediator;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

public class IntermediatorClient{

    private static final String REST_URI_Freezer = "http://localhost:8082/";
    private static final String REST_URI_BoxDB = "http://localhost:8083/";

    private static Client client = ClientBuilder.newClient();


    public static Response insertFreezer(String UserID, String BoxID, int xPos, int yPos){

        return client.target(REST_URI_Freezer + "insert?UserID=" + UserID + "&BoxID=" + BoxID + "&xPos=" + xPos + "&yPos=" + yPos).request().get();
    }


    public static Response retrieveFreezer(String UserID, String BoxID, int xPos, int yPos){

        return client.target(REST_URI_Freezer + "retrieve?UserID=" + UserID + "&BoxID=" + BoxID + "&xPos=" + xPos + "&yPos=" + yPos).request().get();
    }


    public static Response getBoxDB(String UserID, String BoxID){

        return client.target(REST_URI_BoxDB + "get?UserID=" + UserID + "&BoxID=" + BoxID).request().get();
    }


    public static Response insertBoxDB(String UserID, String BoxID, int xPos, int yPos){

        return client.target(REST_URI_BoxDB + "insert?UserID=" + UserID + "&BoxID=" + BoxID + "&xPos=" + xPos + "&yPos=" + yPos).request().get();
    }


    public static Response retrieveBoxDB(String UserID, String BoxID){

        return client.target(REST_URI_BoxDB + "retrieve?UserID=" + UserID + "&BoxID=" + BoxID).request().get();
    }


    public static Response findEmptySlotBoxDB(String UserID, String BoxID){

        return client.target(REST_URI_BoxDB + "findEmptySlot?UserID=" + UserID + "&BoxID=" + BoxID).request().get();
    }


    public static Response getIDBoxDB(String UserID, String BoxID, int xPos, int yPos){

        return client.target(REST_URI_BoxDB + "getID?UserID=" + UserID + "&BoxID=" + BoxID + "&xPos=" + xPos + "&yPos=" + yPos).request().get();
    }

}