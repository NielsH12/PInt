package dk.nielshvid.intermediary;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.EmptyStackException;
import java.util.UUID;
import dk.nielshvid.intermediary.Entities;

//import static dk.nielshvid.intermediary.Entities.EntityType.SAMPLE;

public class InformationService implements InformationServiceInterface{
    private static String connectionUrl = "jdbc:sqlserver://localhost;user=jba;password=123";
    private static Client client = ClientBuilder.newClient();
    private static Gson gson = new Gson();
//    private static Entities.Person person;
    private static Entities.Sample sample;
    private static Entities.Blood blood;
//    private static Entities.Pizza pizza;

//    @Override
//    public String getRole(String UserID, String EntityID){
//
//        UUID OrgID;
//        try {
//            OrgID = UUID.fromString(EntityID.substring(37));
//        }
//        catch (Exception e){
//            return null;
//        }
//
//        String roleResult = findRole(UserID, OrgID);
//
//        if(roleResult!= null){
//            return roleResult;
//        }
//
//        String parrentOrgResult = findParrentOrg(OrgID);
//        if (parrentOrgResult == null){
//            return null;
//        }
//
//        return getRole(UserID, parrentOrgResult);
//    }

    @Override
//    public Entities.EntityType getEntityType(String EntityID) {
    public String getEntityType(MultivaluedMap<String, String> QPmap) {
//        return SAMPLE;
        return "Sample";
    }

    @Override
    public Entities.Sample getSample(String id){
        Response response = client.target("127.0.0.1/" + "getSample?SampleID=" + id).request().get();

        String readEntity = response.readEntity(String.class);

        sample = gson.fromJson(readEntity, Entities.Sample.class);
        return sample;
    }

    @Override
    public Entities.Blood getBlood(String id){
        Response response = client.target("127.0.0.1/" + "getSample?SampleID=" + id).request().get();

        String readEntity = response.readEntity(String.class);

        blood = gson.fromJson(readEntity, Entities.Blood.class);
        return blood;
    }

//    @Override
//    public Entities.Pizza getPizza(String id){
//        Response response = client.target("127.0.0.1/" + "getPizza?PizzaID=" + id).request().get();
//
//        String readEntity = response.readEntity(String.class);
//
//        pizza = gson.fromJson(readEntity, Entities.Pizza.class);
//        return pizza;
//    }

//    @Override
//    public Entities.Person getPerson(String id){
//        Response response = client.target("127.0.0.1/" + "getPerson?PersonID=" + id).request().get();
//
//        String readEntity = response.readEntity(String.class);
//
//        person = gson.fromJson(readEntity, Entities.Person.class);
//        return person;
//    }


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
    @Override //getRoleFromFFU
    public String getRoleByOrganization(String UserID, String OrganizationID){
        return null;
    }

    @Test
    //getRoleFromFFU
    public String getPhysicalsets(){

        String EntityID = "0d403b82b42785881e0aff5c2800c516";
        String authenticateBody = "{\n" +
                "\"username\":\"sJespersen\",\n" +
                "\"password\": \"sJespersen$FFU\"\n" +
                "}\n";
        Invocation.Builder builder = client
                .target("http://tek-ffu-h0a.tek.sdu.dk:80/")
                .path("biostore/authenticate/login")
                .request()
                .header("Content-Type", "application/json");

        Response auth = builder.post(Entity.json(authenticateBody));


        String output = auth.readEntity(String.class);
        System.out.println(output);

        final Cookie sessionId = auth.getCookies().get("connect.sid");


        Invocation.Builder builderLogicalsets = client
                .target("http://tek-ffu-h0a.tek.sdu.dk:80/biostore/physicalsets/"+ EntityID)
                .request()
                .cookie(sessionId);

        Response responseLogic = builderLogicalsets.get();
        output = responseLogic.readEntity(String.class);

        JSONObject jsonOb = null;
        try {
            jsonOb = new JSONObject(output);

            Gson gson = new Gson();

            Entities.Physicalsets physicalsets = gson.fromJson(output, Entities.Physicalsets.class);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        System.out.println(output);
        return null;
    }

    @Override //getRoleFromFFU
    public String getRoleByEntity(String UserID, String EntityID){

        if(true){
            return "Observer";
        }
        String authenticateBody = "{\n" +
                "\"username\":\"sJespersen\",\n" +
                "\"password\": \"sJespersen$FFU\"\n" +
                "}\n";
        Invocation.Builder builder = client
                .target("http://tek-ffu-h0a.tek.sdu.dk:80/")
                .path("biostore/authenticate/login")
                .request()
                .header("Content-Type", "application/json");

        Response auth = builder.post(Entity.json(authenticateBody));


        String output = auth.readEntity(String.class);
        System.out.println(output);

        final Cookie sessionId = auth.getCookies().get("connect.sid");


        Invocation.Builder builderLogicalsets = client
                .target("http://tek-ffu-h0a.tek.sdu.dk:80/biostore/logicalsets/"+ EntityID)
                .request()
                .cookie(sessionId);

        Response responseLogic = builderLogicalsets.get();
        output = responseLogic.readEntity(String.class);

        JSONObject jsonOb = null;
        try {
            jsonOb = new JSONObject(output);
            String creator = jsonOb.getString("creator");
            String creatorID = creator.substring(creator.lastIndexOf(":") + 1);

            // find the group of the owner of the entity
            String groupID = findGroupID(sessionId, creatorID);

            // find the groups that the user is a member of
            JSONArray roleinGroups = findUserGroups(sessionId, UserID);



            int i = 0;
            while (i < roleinGroups.length()) {
                if(roleinGroups.getJSONObject(i).getString("group").equals(groupID)){
                    String roleID = roleinGroups.getJSONObject(i).getString("groupRole");
                    System.out.println("IT IS A MATCH");
                    System.out.println("USERS ROLEID IS: " + roleID);

                    String roleName = getRole2(sessionId, roleID);
                    System.out.println("USERS ROLENAME IS: " + roleName);
                    return roleName;
                }
                i++;
            }

            System.out.println(jsonOb);
            System.out.println(creatorID);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        System.out.println(output);
        return null;
    }

    private String getRole2(Cookie sessionId, String roleID){
        Invocation.Builder builder = client
                .target("http://tek-ffu-h0a.tek.sdu.dk:80/biostore/roles/"+ roleID)
                .request()
                .cookie(sessionId);

        Response response = builder.get();
        String output = response.readEntity(String.class);

        JSONObject jsonOb = null;
        String role = null;
        try {
            jsonOb = new JSONObject(output);
            role = jsonOb.getString("name");

            System.out.println(role);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return role;
    }

    private String findGroupID(Cookie sessionId, String UserID){
        Invocation.Builder builder = client
                .target("http://tek-ffu-h0a.tek.sdu.dk:80/biostore/users/"+ UserID)
                .request()
                .cookie(sessionId);

        Response response = builder.get();
        String output = response.readEntity(String.class);

        JSONObject jsonOb = null;
        String groupID = null;
        try {
            jsonOb = new JSONObject(output);
            JSONArray roleInGroup = jsonOb.getJSONArray("roleInGroup");
            if(roleInGroup.length() <= 0){
                return null;
            }
            JSONObject groupAndGroupRole = roleInGroup.getJSONObject(0);
            groupID = groupAndGroupRole.get("group").toString();

            System.out.println(groupID);



        } catch (JSONException e) {
            e.printStackTrace();
        }
        return groupID;
    }

    private JSONArray findUserGroups(Cookie sessionId, String UserID){
        Invocation.Builder builder = client
                .target("http://tek-ffu-h0a.tek.sdu.dk:80/biostore/users/"+ UserID)
                .request()
                .cookie(sessionId);

        Response response = builder.get();
        String output = response.readEntity(String.class);

        JSONObject jsonOb = null;
        JSONArray roleInGroup = null;
        try {
            jsonOb = new JSONObject(output);
            roleInGroup = jsonOb.getJSONArray("roleInGroup");


            System.out.println(roleInGroup);



        } catch (JSONException e) {
            e.printStackTrace();
        }
        return roleInGroup;
    }
}
