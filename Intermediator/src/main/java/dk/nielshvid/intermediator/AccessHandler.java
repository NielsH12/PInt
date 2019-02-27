package dk.nielshvid.intermediator;


import com.sun.org.apache.xpath.internal.operations.Bool;

import java.sql.*;
import java.util.EmptyStackException;
import java.util.UUID;

public class AccessHandler {
    private static String connectionUrl = "jdbc:sqlserver://localhost;user=jba;password=123";



    public static Boolean CheckAccess(UUID userUUID, String resource, String Action) {


        String role = GetRole(userUUID);
        if(role.equals("")){
            return false;
        }

        if(resource.equals("") || Action.equals("")){
            System.out.println("empty input argument");
            return false;
        }

        String Query = "SELECT COUNT(ID) AS total " +
                "FROM [ffu].[dbo].[Policies] " +
                "WHERE Role = ? AND Resource = ? AND Actions = ?";


        CheckDrivers();

        try (Connection con = DriverManager.getConnection(connectionUrl)) {

            PreparedStatement stmt = con.prepareStatement(Query);


            stmt.setString(1, role);
            stmt.setString(2, resource);
            stmt.setString(3, Action);

            ResultSet rs = stmt.executeQuery();

            if (rs == null){
                System.out.println("rs was null");
                throw new EmptyStackException();
            }

            while(rs.next()) {
                if (rs.getInt("total") > 0){
                    return true;
                }
            }
            return false;
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Got caught on second try-catch (SQL error)");
            return false;
        }
    }

    private static String GetRole(UUID userID) {

        String Query = "SELECT role\n" +
                "FROM [ffu].[dbo].[Persons]\n" +
                "WHERE id = ?";


        CheckDrivers();

        try (Connection con = DriverManager.getConnection(connectionUrl)) {

            PreparedStatement stmt = con.prepareStatement(Query);


            stmt.setString(1, userID.toString().toUpperCase());

            ResultSet rs = stmt.executeQuery();

            // Iterate through the data in the result set and display it.
            if (rs == null){
                System.out.println("rs was null");
                throw new EmptyStackException();
            }

            while(rs.next()) {
                return rs.getString("role");
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Got caught on second try-catch (SQL error)");
            return "";
        }
        return "";
    }
    private static void CheckDrivers() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("SQLServerDriver");
        } catch (Exception e) {
            System.out.println("Got caught on first try-catch (Connection error)");
        }
    }
}