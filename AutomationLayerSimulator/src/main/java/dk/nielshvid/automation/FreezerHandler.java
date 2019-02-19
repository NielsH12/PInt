package dk.nielshvid.automation;

import java.sql.*;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;


public class FreezerHandler {

    private static String connectionUrl = "jdbc:sqlserver://localhost;user=jba;password=123";

    public static boolean updateByPos(int x, int y, String id, boolean closed){

        String Query = "UPDATE [ffu].[dbo].[Freezer] SET boxID = ?, closed = ? WHERE Freezer.x = ? AND Freezer.y = ?";
        return updateFreezerState(Query, x, y, id, closed);
    }

    private static boolean updateFreezerState(String Query, int x, int y, String id, boolean closed){
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("SQLServerDriver");
        } catch (Exception e){

        }
        try (Connection con = DriverManager.getConnection(connectionUrl)) {

            PreparedStatement stmt = con.prepareStatement(Query);

            stmt.setString(1, id);
            stmt.setBoolean(2, closed);
            stmt.setInt(3, x);
            stmt.setInt(4, y);

            int res = stmt.executeUpdate();

            if (res > 0) {
                return true;
            }


        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Freezer[][] GetFreezerState(){

        String Query = "SELECT * FROM [ffu].[dbo].[Freezer]";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("SQLServerDriver");
        } catch (Exception e){

        }
        try (Connection con = DriverManager.getConnection(connectionUrl)) {

            PreparedStatement stmt = con.prepareStatement(Query);

            ResultSet rs = stmt.executeQuery();

            // Iterate through the data in the result set and display it.
            if (rs == null){
                throw new EmptyStackException();
            }

            // List<Freezer> freezers = new ArrayList<>();
            Freezer[][] freezers = new Freezer[4][3];

            while(rs.next()) {
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                String ID = rs.getString("boxID");
                boolean Closed = rs.getBoolean("closed");

                Freezer temp = new Freezer();

                temp.x = x;
                temp.y = y;
                temp.ID = ID;
                temp.Closed = Closed;

                freezers[x][y] = temp;
            }

            return freezers;
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

