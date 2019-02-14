public class TestIntermediator {
    public static void main(String[] args) {

        System.out.println("Intermediator");

        BoxHandler bh = new BoxHandler();

        System.out.println(bh.GetBoxInfoByID(2));
    }
}