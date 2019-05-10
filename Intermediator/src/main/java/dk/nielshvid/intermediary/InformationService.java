package dk.nielshvid.intermediary;

import com.google.gson.Gson;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.EmptyStackException;
import java.util.UUID;

import static dk.nielshvid.intermediary.Entities.EntityTypes.SAMPLE;

public class InformationService implements InformationServiceInterface{
    private static String connectionUrl = "jdbc:sqlserver://localhost;user=jba;password=123";
    private static Client client = ClientBuilder.newClient();
    private static Gson gson = new Gson();
    private static Entities.Person person;
    private static Entities.Sample sample;
    private static Entities.Pizza pizza;

    @Override
    public String getRole(String UserID, String EntityID){
        UUID OrgID;
        try {
            OrgID = UUID.fromString(EntityID.substring(37));
        }
        catch (Exception e){
            return null;
        }

        String roleResult = findRole(UserID, OrgID);

        if(roleResult!= null){
            return roleResult;
        }

        String parrentOrgResult = findParrentOrg(OrgID);
        if (parrentOrgResult == null){
            return null;
        }

        return getRole(UserID, parrentOrgResult);
    }

    @Override
    public Entities.EntityTypes getEntityType(String EntityID) {
        return SAMPLE;
    }

    @Override
    public Entities.Sample getSample(String id){
        Response response = client.target("127.0.0.1/" + "getSample?SampleID=" + id).request().get();

        String readEntity = response.readEntity(String.class);

        sample = gson.fromJson(readEntity, Entities.Sample.class);
        return sample;
    }

    @Override
    public Entities.Pizza getPizza(String id){
        Response response = client.target("127.0.0.1/" + "getPizza?PizzaID=" + id).request().get();

        String readEntity = response.readEntity(String.class);

        pizza = gson.fromJson(readEntity, Entities.Pizza.class);
        return pizza;
    }

    @Override
    public Entities.Person getPerson(String id){
        Response response = client.target("127.0.0.1/" + "getPerson?PersonID=" + id).request().get();

        String readEntity = response.readEntity(String.class);

        person = gson.fromJson(readEntity, Entities.Person.class);
        return person;
    }


    private static String findParrentOrg(UUID OrgID)  {
        CheckDrivers();
        String Query = "SELECT parrentID From [ffu].[dbo].Organization\n" +
                "WHERE id = ?";


        try (Connection con = DriverManager.getConnection(connectionUrl)) {

            PreparedStatement stmt = con.prepareStatement(Query);


            stmt.setString(1, OrgID.toString().toUpperCase());

            ResultSet rs = stmt.executeQuery();

            // Iterate through the data in the result set and display it.
            if (rs == null) {
                System.out.println("rs was null");
                throw new EmptyStackException();
            }

            String result = null;
            while (rs.next()) {
                result = rs.getString("parrentID");
//                result[1] = rs.getString("name");
            }
            return result;
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Got caught on second try-catch (SQL error)");
            return "error";
        }
    }

    private static String findRole(String UserID, UUID OrgID)  {
        CheckDrivers();
        String Query = "SELECT role From [ffu].[dbo].PersonsHasRoleInOrganization\n" +
                "  LEFT JOIN [ffu].[dbo].Roles as R\n" +
                "    ON R.id = [ffu].[dbo].PersonsHasRoleInOrganization.roleId\n" +
                "  LEFT JOIN [ffu].[dbo].Organization as org\n" +
                "            ON org.id = [ffu].[dbo].PersonsHasRoleInOrganization.organizationID\n" +
                "WHERE personId = ? AND organizationID = ?";


        try (Connection con = DriverManager.getConnection(connectionUrl)) {

            PreparedStatement stmt = con.prepareStatement(Query);


            stmt.setString(1, UserID.toUpperCase());
            stmt.setString(2, OrgID.toString().toUpperCase());

            ResultSet rs = stmt.executeQuery();

            // Iterate through the data in the result set and display it.
            if (rs == null) {
                System.out.println("rs was null");
                throw new EmptyStackException();
            }

            String result = null;
            while (rs.next()) {
                result = rs.getString("role");
//                result[1] = rs.getString("name");
            }
            return result;
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Got caught on second try-catch (SQL error)");
            return null;
        }
    }

    private static void CheckDrivers() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("SQLServerDriver");
        } catch (Exception e){
            System.out.println("Got caught on first try-catch (Connection error)");
        }
    }
}
