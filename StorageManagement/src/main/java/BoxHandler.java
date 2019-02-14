import java.sql.*;
import java.util.EmptyStackException;


class BoxHandler {

    private static String connectionUrl = "jdbc:sqlserver://localhost;;user=jba;password=123";

    String GetBoxInfoByID(String BoxID)  {
        String Query = "SELECT * FROM [ffu].[dbo].[Box] WHERE id =?";

        return SendBoxQuery(Query,BoxID);
    }

    private String SendBoxQuery(String Query, String BoxID){
        try (Connection con = DriverManager.getConnection(connectionUrl)) {

            PreparedStatement  stmt = con.prepareStatement(Query);

            stmt.setString(1, BoxID);

            ResultSet rs = stmt.executeQuery();

            // Iterate through the data in the result set and display it.
            if (rs == null){
                throw new EmptyStackException();
            }
            String result = "";
            while(rs.next()){
                result = "" +
                        rs.getString("owner") +  " " +
                        rs.getString("id") + " " +
                        rs.getTimestamp("created") + " " +
                        rs.getTimestamp("accessed") + " " +
                        rs.getTimestamp("expiration") + " " +
                        rs.getInt("posX") + " " +
                        rs.getInt("posY");
            }

            return result;
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

