package dk.nielshvid.intermediator;

import java.sql.*;
import java.util.EmptyStackException;
import java.util.UUID;

public class IdentityService {
    private static String connectionUrl = "jdbc:sqlserver://localhost;user=jba;password=123";

    public String getRole(UUID UserID, UUID OrgID){

        String roleResult = findRole(UserID, OrgID);


        if(roleResult!= null){
            return roleResult;
        }

        String parrentOrgResult = findParrentOrg(OrgID);
        if (parrentOrgResult == null){
            return null;
        }

        return getRole(UserID, UUID.fromString(parrentOrgResult));
    }

    private String findParrentOrg(UUID OrgID)  {
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

    private String findRole(UUID UserID, UUID OrgID)  {
        CheckDrivers();
        String Query = "SELECT role From [ffu].[dbo].PersonsHasRoleInOrganization\n" +
                "  LEFT JOIN [ffu].[dbo].Roles as R\n" +
                "    ON R.id = [ffu].[dbo].PersonsHasRoleInOrganization.roleId\n" +
                "  LEFT JOIN [ffu].[dbo].Organization as org\n" +
                "            ON org.id = [ffu].[dbo].PersonsHasRoleInOrganization.organizationID\n" +
                "WHERE personId = ? AND organizationID = ?";


        try (Connection con = DriverManager.getConnection(connectionUrl)) {

            PreparedStatement stmt = con.prepareStatement(Query);


            stmt.setString(1, UserID.toString().toUpperCase());
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
