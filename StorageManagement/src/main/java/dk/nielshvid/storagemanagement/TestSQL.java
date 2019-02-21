package dk.nielshvid.storagemanagement;

import java.util.UUID;

public class TestSQL {
    public static void main(String[] args) {

        FindEmptySlot();

    }

    static void FindEmptySlot(){
        FreezerStateHandler freezerStateHandler = new FreezerStateHandler();

        int[] result = freezerStateHandler.FindEmptySlot();
        System.out.println(result[0] + " " + result[1]);
    }

    static void GetBox(){
        BoxHandler boxHandler = new BoxHandler();

        dbBox result = boxHandler.GetBoxInfoByID(UUID.fromString("A70D717E-935E-4CA2-8192-22E65D84BF71"));

        System.out.println(result.created);
    }

    static void testOpen(){
        BoxHandler boxHandler = new BoxHandler();

        int result = boxHandler.RetrieveBoxByID(UUID.fromString("A70D717E-935E-4CA2-8192-22E65D84BF71"));

        System.out.println(result);
    }
}
