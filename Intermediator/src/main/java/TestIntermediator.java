public class TestIntermediator {
    public static void main(String[] args) {

        System.out.println("Intermediator");

        BoxHandler bh = new BoxHandler();

        dbBox result = bh.GetBoxInfoByID("A70D717E-935E-4CA2-8192-22E65D84BF71");

        System.out.println(result.created);
       // System.out.println(bh.GetBoxInfoByID("A70D717E-935E-4CA2-8192-22E65D84BF71"));
    }
}