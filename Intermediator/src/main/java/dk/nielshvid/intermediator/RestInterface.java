package dk.nielshvid.intermediator;

//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;

//import dk.nielshvid.storagemanagement.BoxHandler;
//import dk.nielshvid.storagemanagement.dbBox;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
public class RestInterface {
    @Path("hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String u() {
        return "niels";
    }

    @Path("get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@QueryParam("ID") String ID)  {
        return Intermediator.lookup(ID);
    }

    @Path("open")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String open(@QueryParam("ID") String ID)  {
        return Intermediator.lookup(ID);
    }

    @Path("insert")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String insert(@QueryParam("ID") String ID)  {
        return Intermediator.lookup(ID);
    }
}
