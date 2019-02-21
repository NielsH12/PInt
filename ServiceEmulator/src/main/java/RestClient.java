import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;

public class RestClient {

    private static final String REST_URI = "http://localhost:8081/";
    private static Client client = ClientBuilder.newClient();

    public static Response test(){
        return client.target(REST_URI).request(MediaType.TEXT_PLAIN).get();
    }

    public static Response getBoxInfoByID(String ID){
        return client.target(REST_URI + "get?ID=" + ID).request().get(); // GET
    }

    public static Response openBoxByID(String ID){
        return client.target(REST_URI + "open?ID=" + ID).request().get(); // OPEN
    }

    public static Response insertBoxByID(String ID){
        return client.target(REST_URI + "insert?ID=" + ID).request().get(); // INSERT
    }

    public static void main(String[] args){
        System.out.println(test().readEntity(String.class));
    }
}

// https://www.baeldung.com/jersey-jax-rs-client