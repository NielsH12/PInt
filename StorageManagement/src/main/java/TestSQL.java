import java.sql.*;

public class TestSQL {
    public static void main(String[] args) {

        BoxHandler boxHandler = new BoxHandler();

        String result = boxHandler.GetBoxInfoByID(2);

        System.out.println(result);

    }
}
