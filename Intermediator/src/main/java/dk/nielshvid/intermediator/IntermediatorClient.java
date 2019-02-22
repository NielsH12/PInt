package dk.nielshvid.intermediator;

import dk.nielshvid.storagemanagement.BoxHandler;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.UUID;

public class IntermediatorClient {
    private static final String REST_URI = "http://localhost:8082/";
    private static Client client = ClientBuilder.newClient();
    private static BoxHandler boxHandler = new BoxHandler();

    public static String RetrieveBox(UUID id, int xPos, int yPos){
        Response response = client.target(REST_URI + "retrieve?xPos=" + xPos + "&yPos=" + yPos).request().get();

        if(response.getStatus() != Response.Status.OK.getStatusCode()){
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        int sqlResponse = boxHandler.RetrieveBoxByID(id);
        if(sqlResponse <= 0){
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return "Success";
    }

    public static String InsertBox(UUID id, int xPos, int yPos){

        Response response = client.target(REST_URI + "insert?ID=" + id + "xPos=" + xPos + "yPos=" + yPos).request().get();

        if(response.getStatus() != Response.Status.OK.getStatusCode()){
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return "Success";
    }

    // return client.target(REST_URI).request(MediaType.APPLICATION_JSON).post(Entity.entity(emp, MediaType.APPLICATION_JSON));
//
//    public static Response openBoxByID(String ID){
//        return client.target(REST_URI + "retrieve?ID=" + ID).request(MediaType.APPLICATION_JSON_TYPE).get();
//    }
//
//    public static Response insertBoxByID(String ID){
//        return client.target(REST_URI + "insert?ID=" + ID).request(MediaType.APPLICATION_JSON_TYPE).get();
//    }

}
