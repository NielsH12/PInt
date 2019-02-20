package dk.nielshvid.storagemanagement;

import java.sql.*;
import java.util.EmptyStackException;


public class BoxHandler {

    private static String connectionUrl = "jdbc:sqlserver://localhost;user=jba;password=123";

    public BoxHandler(){

    }



    public dbBox GetBoxInfoByID(String BoxID)  {

        String Query = "SELECT Box.id" +
        ", Persons.firstName" +
        ", Persons.lastName" +
        ", Persons.email" +
        ", created" +
        ", accessed" +
        ", expiration" +
        ", posX" +
        ", posY " +
        "FROM [ffu].[dbo].[Box] INNER JOIN [ffu].[dbo].[Persons] ON Box.owner = Persons.id WHERE Box.id = ?";

        return SendBoxQuery(Query,BoxID);
    }

    private dbBox SendBoxQuery(String Query, String BoxID){
        System.out.println("Sending box query for box: " + BoxID);
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("SQLServerDriver");
        } catch (Exception e){
            System.out.println("Got caught on first try-catch (Connection error)");
        }
        try (Connection con = DriverManager.getConnection(connectionUrl)) {


            PreparedStatement stmt = con.prepareStatement(Query);

            stmt.setString(1, BoxID);

            ResultSet rs = stmt.executeQuery();

            // Iterate through the data in the result set and display it.
            if (rs == null){
                System.out.println("rs was null");
                throw new EmptyStackException();
            }

            dbBox result = new dbBox();
            while(rs.next()) {
                result.id = rs.getString("id");
                result.firstName = rs.getString("firstName");
                result.lastName = rs.getString("lastName");
                result.email = rs.getString("email");
                result.created = rs.getTimestamp("created");
                result.accessed = rs.getTimestamp("accessed");
                result.expiration = rs.getTimestamp("expiration");
                result.posX = rs.getInt("posX");
                result.posY = rs.getInt("posY");
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
}

