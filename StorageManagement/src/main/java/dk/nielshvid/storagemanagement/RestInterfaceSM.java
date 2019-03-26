package dk.nielshvid.storagemanagement;

import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//http://programmerscuriosity.com/2016/09/27/simple-jersey-example-with-intellij-idea-ultimate-and-tomcat/

@Path("/")
public class RestInterfaceSM {
    private Gson gson = new Gson();

    @Path("hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String u() {
        System.out.println("we pressed get");
        return "fisk";
    }

    @Path("findEmptySlot")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String findEmptySlot()  {
        int[] result = FreezerHandler.FindEmptySlot();

        return gson.toJson(result);
    }

    @Path("insert")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String insert(@QueryParam("BoxID") String BoxID, @QueryParam("xPos") int x,@QueryParam("yPos") int y)  {
        int result = FreezerHandler.InsertID(BoxID, x, y);

        return Integer.toString(result);
    }

    @Path("get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@QueryParam("BoxID") String BoxID)  {
        if(BoxID == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        dbBox result = new BoxHandler().GetBoxInfoByID(BoxID);

        if(result.id == null){
            throw new WebApplicationException(Response.status(204).entity("Box do not exist").type("text/plain").build());
        }

        return gson.toJson(result);
    }

    @Path("retrieve")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String retrieve(@QueryParam("BoxID") String BoxID)  {
        if(BoxID == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        int result = new BoxHandler().RetrieveBoxByID(BoxID);

        if(result == 0){
            throw new WebApplicationException(Response.status(204).entity("Box do not exist").type("text/plain").build());
        }
        if(result != 1){
            throw new WebApplicationException(Response.status(418).entity("StorageManager fucked up").type("text/plain").build());
        }

        return "Success";
    }
}
