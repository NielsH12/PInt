package dk.nielshvid.automation;

import java.sql.*;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;


public class PhysicalFreezerHandler {

    private static String connectionUrl = "jdbc:sqlserver://localhost;user=jba;password=123";


    public static boolean updateByPos(int x, int y, String EntityID){
        String _EntityID = null;
        if(EntityID != null){
            _EntityID = EntityID.substring(0,36);
        }

        String Query = "UPDATE [ffu].[dbo].[PhysicalFreezer] SET EntityID = ? WHERE PhysicalFreezer.x = ? AND PhysicalFreezer.y = ?";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (Exception e){

        }
        try (Connection con = DriverManager.getConnection(connectionUrl)) {

            PreparedStatement stmt = con.prepareStatement(Query);

            stmt.setString(1, _EntityID);
            stmt.setInt(2, x);
            stmt.setInt(3, y);

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

    public static Freezer[][] GetPhysicalFreezerState(){

        String Query = "SELECT * FROM [ffu].[dbo].[PhysicalFreezer]";

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
                String ID = rs.getString("EntityID");

                Freezer temp = new Freezer();

                temp.x = x;
                temp.y = y;
                temp.ID = ID;

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

