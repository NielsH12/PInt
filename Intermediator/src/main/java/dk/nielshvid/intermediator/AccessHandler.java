package dk.nielshvid.intermediator;

import java.sql.*;
import java.util.*;

public class AccessHandler {
    private static String connectionUrl = "jdbc:sqlserver://localhost;user=jba;password=123";



    public static Boolean CheckAccess(String _userUUID, String resource, String Action) {

        // language=SQL
        String Query = "SELECT * From\n" +
                "(SELECT * FROM Policies WHERE Resource = ? AND Actions = ?) as P\n" +
                "  INNER JOIN PersonsHaveRole PHR\n" +
                "             ON PHR.RoleId = P.RoleId_P\n" +
                "  INNER JOIN Roles R\n" +
                "             ON PHR.roleId = R.id\n" +
                "WHERE PHR.personId = ?";

        CheckDrivers();

        try (Connection con = DriverManager.getConnection(connectionUrl)) {

            PreparedStatement stmt = con.prepareStatement(Query);


            stmt.setString(1, resource);
            stmt.setString(2, Action);
            stmt.setString(3, _userUUID);

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

//    private static  List<String> GetRole(UUID userID) {
//
//        String Query2 = "SELECT role\n" +
//                "FROM [ffu].[dbo].[Persons]\n" +
//                "WHERE id = ?";
//
//        // language=SQL
//        String Query = "SELECT Roles.id\n" +
//                "FROM Persons \n" +
//                "       INNER JOIN PersonsHaveRole PHR\n" +
//                "         ON Persons.id = PHR.personId\n" +
//                "       INNER JOIN Roles\n" +
//                "         ON PHR.roleId = Roles.id \n" +
//                "WHERE role = ? AND organization = ?";
//
//        CheckDrivers();
//
//        try (Connection con = DriverManager.getConnection(connectionUrl)) {
//
//            PreparedStatement stmt = con.prepareStatement(Query);
//
//
//            stmt.setString(1, userID.toString().toUpperCase());
//
//            ResultSet rs = stmt.executeQuery();
//
//            // Iterate through the data in the result set and display it.
//            if (rs == null){
//                System.out.println("rs was null");
//                throw new EmptyStackException();
//            }
//
//            List<String[]> Roles = new ArrayList<>();
//            while(rs.next()) {
//                String[] fisk = new String[2];
//                fisk[0] = rs.getString("role");
//                fisk[1] = rs.getString("id");
//                Roles.add(fisk);
//            }
//
//            return Roles;
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//            System.out.println("Got caught on second try-catch (SQL error)");
//            return  Collections.emptyList();
//        }
//    }

    private static void CheckDrivers() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("SQLServerDriver");
        } catch (Exception e) {
            System.out.println("Got caught on first try-catch (Connection error)");
        }
    }
}