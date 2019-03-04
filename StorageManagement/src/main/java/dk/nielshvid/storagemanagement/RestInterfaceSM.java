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

    @Path("findEmptySlot")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String findEmptySlot()  {
        int[] result = FreezerStateHandler.FindEmptySlot();

        return gson.toJson(result);
    }

    @Path("insert")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String insert(@QueryParam("ID") UUID id, @QueryParam("xPos") int x,@QueryParam("yPos") int y)  {
        int result = FreezerStateHandler.InsertID(id, x, y);

        return Integer.toString(result);
    }

    @Path("get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@QueryParam("ID") UUID ID)  {
        if(ID == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        dbBox result = new BoxHandler().GetBoxInfoByID(ID);

        if(result.id == null){
            throw new WebApplicationException(Response.status(204).entity("Box do not exist").type("text/plain").build());
        }

        return gson.toJson(result);
    }

    @Path("retrieve")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String retrieve(@QueryParam("ID") UUID ID)  {
        if(ID == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        int result = new BoxHandler().RetrieveBoxByID(ID);

        if(result == 0){
            throw new WebApplicationException(Response.status(204).entity("Box do not exist").type("text/plain").build());
        }
        if(result != 1){
            throw new WebApplicationException(Response.status(418).entity("StorageManager fucked up").type("text/plain").build());
        }

        return "Success";
    }

    //    @Path("getBoxInfoByID")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public String getBoxInfoByID(@QueryParam("ID") UUID ID)  {
//        if(ID == null) {
//            throw new WebApplicationException(Response.Status.BAD_REQUEST);
//        }
//
//        dbBox result = new BoxHandler().GetBoxInfoByID(ID);
//
//        if(result.id == null){
//            throw new WebApplicationException(Response.status(204).entity("Box do not exist").type("text/plain").build());
//        }
//
//        return gson.toJson(result);
//    }

//    @Path("getBoxPosition")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public String getBoxPosition(@QueryParam("ID") UUID ID)  {
//        if(ID == null) {
//            throw new WebApplicationException(Response.Status.BAD_REQUEST);
//        }
//
//        dbBox result = new BoxHandler().GetBoxInfoByID(ID);
//        if(result.id == null){
//            throw new WebApplicationException(Response.status(204).entity("Box do not exist").type("text/plain").build());
//        }
//        if(result.posX == null || result.posY == null){
//            throw new WebApplicationException(Response.status(204).entity("Box is NOT in Freezer").type("text/plain").build());
//        }
//
//        int[] resultPos = new int[2];
//        resultPos[0] = result.posX;
//        resultPos[1] = result.posY;
//
//        Gson gs = new Gson();
//
//        return gs.toJson(resultPos);
//    }
}
