package dk.nielshvid.storagemanagement;

import java.sql.*;
import java.util.EmptyStackException;

public class FreezerStateHandler {
    private static String connectionUrl = "jdbc:sqlserver://localhost;user=jba;password=123";

    //Retrieve are in the boxHandler dont ask why!?


    public static int InsertID(String _BoxID, int x, int y){

        String BoxID = _BoxID.substring(0,36);

        CheckDrivers();

        String Query = "UPDATE [ffu].[dbo].FreezerState SET boxID=? WHERE x =? AND y =?";

        try (Connection con = DriverManager.getConnection(connectionUrl)) {

            PreparedStatement stmt = con.prepareStatement(Query);

            stmt.setString(1, BoxID);
            stmt.setInt(3, y);
            stmt.setInt(2,x);

            int rs = stmt.executeUpdate();

            if (rs != 1){
                System.out.println("Updated unexpected number of rows: " + rs);
            }
            return rs;
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Got caught on second try-catch (SQL error)");
            return -1;
        }

    }


    public static int[] FindEmptySlot()  {
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
