package dk.nielshvid.intermediator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import dk.nielshvid.storagemanagement.*;

public class Intermediator {
    //private static BoxHandler bh = new BoxHandler();
    private static ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    static String lookup(String id){
        dbBox t = BoxHandler.getInstance().GetBoxInfoByID(id);
        String json = "";
        try {
            //json = "hej";
            json = ow.writeValueAsString(t);
        } catch ( Exception e){
            System.out.println("Oh snap");
        }

        return json;
    }
}
