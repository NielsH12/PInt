package dk.nielshvid.storagemanagement;

import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

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


    @Path("getBoxInfoByID")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getBoxInfoByID(@QueryParam("ID") UUID ID)  {
        if(ID == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        dbBox result = new BoxHandler().GetBoxInfoByID(ID);

        if(result.id == null){
            throw new WebApplicationException(Response.status(204).entity("Box do not exist").type("text/plain").build());
        }

        return gson.toJson(result);
    }

    @Path("getBoxPosition")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getBoxPosition(@QueryParam("ID") UUID ID)  {
        if(ID == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        dbBox result = new BoxHandler().GetBoxInfoByID(ID);
        if(result.id == null){
            throw new WebApplicationException(Response.status(204).entity("Box do not exist").type("text/plain").build());
        }
        if(result.posX == null || result.posY == null){
            throw new WebApplicationException(Response.status(204).entity("Box is NOT in Freezer").type("text/plain").build());
        }

        int[] resultPos = new int[2];
        resultPos[0] = result.posX;
        resultPos[1] = result.posY;

        Gson gs = new Gson();

        return gs.toJson(resultPos);
    }

    @Path("findEmptySlot")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String findEmptySlot()  {
        int[] result = FreezerStateHandler.FindEmptySlot();

        return gson.toJson(result);
    }

    @Path("InsertBox")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String InsertBox(@QueryParam("ID") UUID id, @QueryParam("posX") int x,@QueryParam("posY") int y)  {
        int result = FreezerStateHandler.InsertID(id, x, y);

        return Integer.toString(result);
    }
}
