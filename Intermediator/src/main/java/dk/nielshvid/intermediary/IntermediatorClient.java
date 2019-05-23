package dk.nielshvid.intermediary;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Map;

public class IntermediatorClient {

    private static final String REST_URI_Freezer = "http://localhost:8082/";
    private static final String REST_URI_BoxDB = "http://localhost:8083/";
    private static final String REST_URI_FFU = "http://tek-ffu-h0a.tek.sdu.dk:80/biostore/"; //FFU

    private static Client client = ClientBuilder.newClient();
    private static Cookie sessionId = null;

    public IntermediatorClient(){
        String authenticateBody = "{\n" +
                "\"username\":\"sJespersen\",\n" +
                "\"password\": \"sJespersen$FFU\"\n" +
                "}\n";

        Response authenticate = client
                .target("http://tek-ffu-h0a.tek.sdu.dk:80/biostore/authenticate/login")
                .request()
                .header("Content-Type", "application/json")
                .post(Entity.json(authenticateBody));
        String output = authenticate.readEntity(String.class);
        System.out.println(output);
        sessionId = authenticate.getCookies().get("connect.sid");
    }

    /// FFU

    public static Response authorizeFreezer(String UserID, String EntityID, MultivaluedMap<String, String> qmap){
        return client.target(REST_URI_FFU + "authorize?UserID=" + UserID + "&EntityID=" + EntityID).request().get();
    }


    public static Response getFreezer(String UserID, String EntityID, MultivaluedMap<String, String> qmap){
        return client.target(REST_URI_FFU + "get?UserID=" + UserID + "&EntityID=" + EntityID).request().get();
    }


    public static Response setFreezer(String UserID, String EntityID, MultivaluedMap<String, String> qmap){
        return client.target(REST_URI_FFU + "set?UserID=" + UserID + "&EntityID=" + EntityID).request().get();
    }

    public Response getLogicalSets(UriInfo info, String body, HttpHeaders headers){
        Invocation.Builder builder = client.target("http://tek-ffu-h0a.tek.sdu.dk:80")
                .path("biostore/logicalsets")
                .request()
                .header("Content-Type", "application/json");

                for(Map.Entry<String, Cookie> entry : headers.getCookies().entrySet()){
                    builder.cookie(entry.getValue());
                }


        return builder.put(Entity.json(body));
    }

    /// FFU

    public Response insertFreezer(String UserID, String EntityID, MultivaluedMap<String, String> qmap){
        String xPos = qmap.getFirst("xPos");
        String yPos = qmap.getFirst("yPos");

        return client.target(REST_URI_Freezer + "insert?UserID=" + UserID + "&EntityID=" + EntityID + "&xPos=" + xPos + "&yPos=" + yPos).request().get();
    }

    public Response retrieveFreezer(String UserID, String EntityID, MultivaluedMap<String, String> qmap){
        String xPos = qmap.getFirst("xPos");
        String yPos = qmap.getFirst("yPos");

        return client.target(REST_URI_Freezer + "retrieve?UserID=" + UserID + "&EntityID=" + EntityID + "&xPos=" + xPos + "&yPos=" + yPos).request().get();
    }

    public Response getBoxDB(String UserID, String EntityID, MultivaluedMap<String, String> qmap){

        return client.target(REST_URI_BoxDB + "get?UserID=" + UserID + "&EntityID=" + EntityID).request().get();
    }

    public Response insertBoxDB(String UserID, String EntityID, MultivaluedMap<String, String> qmap){
        String xPos = qmap.getFirst("xPos");
        String yPos = qmap.getFirst("yPos");

        return client.target(REST_URI_BoxDB + "insert?UserID=" + UserID + "&EntityID=" + EntityID + "&xPos=" + xPos + "&yPos=" + yPos).request().get();
    }

    public Response retrieveBoxDB(String UserID, String EntityID, MultivaluedMap<String, String> qmap){

        return client.target(REST_URI_BoxDB + "retrieve?UserID=" + UserID + "&EntityID=" + EntityID).request().get();
    }

    public Response findEmptySlotBoxDB(String UserID, String EntityID, MultivaluedMap<String, String> qmap){

        return client.target(REST_URI_BoxDB + "findEmptySlot?UserID=" + UserID + "&EntityID=" + EntityID).request().get();
    }

    public Response getIDBoxDB(String UserID, String EntityID, MultivaluedMap<String, String> qmap){
        String xPos = qmap.getFirst("xPos");
        String yPos = qmap.getFirst("yPos");

        return client.target(REST_URI_BoxDB + "getID?UserID=" + UserID + "&EntityID=" + EntityID + "&xPos=" + xPos + "&yPos=" + yPos).request().get();
    }
}