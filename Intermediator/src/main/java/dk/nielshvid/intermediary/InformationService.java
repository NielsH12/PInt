package dk.nielshvid.intermediary;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/////////////////////////////////////////
// This class is only for FFU use case //
/////////////////////////////////////////
public class InformationService implements InformationServiceInterface{
    private static Client client = ClientBuilder.newClient();
    private static Gson gson = new Gson();
    private static Entities.Sample sample;


    @Override
    public String getEntityType(MultivaluedMap<String, String> QPmap) {
        return "Sample";
    }

    @Override
    public Entities.Sample getSample(String id){
        return null;
    }

    @Override //getRoleFromFFU
    public String getRoleByOrganization(String UserID, String OrganizationID){
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

                    String roleName = getRole(sessionId, roleID);
                    System.out.println("USERS ROLENAME IS: " + roleName);
                    return roleName;
                }
                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(output);
        return null;
    }

    private String getRole(Cookie sessionId, String roleID){
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
