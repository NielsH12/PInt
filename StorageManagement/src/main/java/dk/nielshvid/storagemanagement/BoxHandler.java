package dk.nielshvid.storagemanagement;

import java.sql.*;
import java.util.EmptyStackException;
import java.util.UUID;


public class BoxHandler {

    private static String connectionUrl = "jdbc:sqlserver://localhost;user=jba;password=123";

    public BoxHandler(){

    }

    public dbBox GetBoxInfoByID(String BoxID)  {

        String _BoxID = BoxID.substring(0,36);
        String _OrgID = BoxID.substring(37);

        String Query = "SELECT Box.id, P.firstName, P.lastName, P.email, created, accessed, expiration, FS.x, FS.y, Box.organizationID\n" +
                "FROM [ffu].[dbo].[Box]\n" +
                "LEFT OUTER JOIN [ffu].[dbo].[Freezer] FS on Box.id = FS.boxID\n" +
                "INNER JOIN [ffu].[dbo].[Persons] P on Box.owner = P.id\n" +
                "WHERE Box.id = ? AND Box.organizationID = ?";


        CheckDrivers();
        try (Connection con = DriverManager.getConnection(connectionUrl)) {

            PreparedStatement stmt = con.prepareStatement(Query);

            System.out.println(BoxID.toString());

            stmt.setString(1, _BoxID);
            stmt.setString(2, _OrgID);

            ResultSet rs = stmt.executeQuery();

            // Iterate through the data in the result set and display it.
            if (rs == null){
                System.out.println("rs was null");
                throw new EmptyStackException();
            }

            dbBox result = new dbBox();
            while(rs.next()) {
                result.id = rs.getString("id")+ "@" +rs.getString("organizationID");
                result.firstName = rs.getString("firstName");
                result.lastName = rs.getString("lastName");
                result.email = rs.getString("email");
                result.created = rs.getTimestamp("created");
                result.accessed = rs.getTimestamp("accessed");
                result.expiration = rs.getTimestamp("expiration");
                result.posX = (Integer) rs.getObject("x");
                result.posY = (Integer) rs.getObject("y");
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

    public int RetrieveBoxByID(String BoxID)  {

        String _BoxID = BoxID.substring(0,36);

        String Query = "BEGIN TRY\n" +
                "BEGIN TRANSACTION\n" +
                "        UPDATE [ffu].[dbo].[Freezer] SET boxID = null WHERE boxID =?\n" +
                "        UPDATE [ffu].[dbo].[Box] SET accessed = GETDATE() WHERE id =?\n" +
                "COMMIT\n" +
                "END TRY\n" +
                "BEGIN CATCH\n" +
                "        ROLLBACK\n" +
                "END CATCH";

        CheckDrivers();
        try (Connection con = DriverManager.getConnection(connectionUrl)) {

            PreparedStatement stmt = con.prepareStatement(Query);

            stmt.setString(1, _BoxID);
            stmt.setString(2, _BoxID);

            return stmt.executeUpdate();
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Got caught on second try-catch (SQL error)");
            return 0;
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

