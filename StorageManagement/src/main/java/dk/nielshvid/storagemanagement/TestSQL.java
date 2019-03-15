package dk.nielshvid.storagemanagement;

public class TestSQL {
    public static void main(String[] args) {

        FindEmptySlot();
        GetBox();
//        testOpen();

    }

    static void FindEmptySlot(){
        FreezerStateHandler freezerStateHandler = new FreezerStateHandler();

        int[] result = freezerStateHandler.FindEmptySlot();
        System.out.println(result[0] + " " + result[1]);
    }

    static void GetBox(){
        BoxHandler boxHandler = new BoxHandler();

        dbBox result = boxHandler.GetBoxInfoByID("A70D717E-935E-4CA2-8192-22E65D84BF71@1E3C9DBF-C004-4F93-B3BB-D1E45945D482");

        System.out.println(result.created);
    }

    static void testOpen(){
        BoxHandler boxHandler = new BoxHandler();

        int result = boxHandler.RetrieveBoxByID("A70D717E-935E-4CA2-8192-22E65D84BF711E3C9DBF-C004-4F93-B3BB-D1E45945D482");

        System.out.println(result);
    }
}
