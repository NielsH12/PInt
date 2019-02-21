package dk.nielshvid.storagemanagement;

import java.sql.*;
import java.util.EmptyStackException;

public class FreezerStateHandler {
    private static String connectionUrl = "jdbc:sqlserver://localhost;user=jba;password=123";

    public int[] FindEmptySlot()  {
        CheckDrivers();

        String Query = "SELECT TOP(1) x,y FROM [ffu].[dbo].[FreezerState] WHERE boxID IS NULL";


        try (Connection con = DriverManager.getConnection(connectionUrl)) {

            PreparedStatement stmt = con.prepareStatement(Query);

            ResultSet rs = stmt.executeQuery();

            if (rs == null){
                System.out.println("rs was null");
                throw new EmptyStackException();
            }

            int[] pos = new int[2];
            pos[0] = -1;
            pos[1] = -1;

            while(rs.next()) {
                pos[0] = rs.getInt("x");
                pos[1] = rs.getInt("y");
            }
            return pos;
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
