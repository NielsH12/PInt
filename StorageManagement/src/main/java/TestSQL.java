import java.sql.*;

public class TestSQL {
    public static void main(String[] args) {

        BoxHandler boxHandler = new BoxHandler();

        String result = boxHandler.GetBoxInfoByID("A70D717E-935E-4CA2-8192-22E65D84BF71");

        System.out.println(result);

    }
}
