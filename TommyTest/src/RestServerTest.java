import javax.ws.rs.*;

@ApplicationPath("/")
public class RestServerTest {

    @GET
    @Path("/score")
    @Produces("text/plain")
    public String getScore() {
        return ("Niels");
    }
}