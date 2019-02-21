package dk.nielshvid.intermediator;

import com.google.gson.Gson;
import dk.nielshvid.storagemanagement.*;
import java.util.UUID;

public class Intermediator {

    private static Gson gs = new Gson();

    static BoxHandler bh = new BoxHandler();

    static String lookup(UUID id){
        dbBox t = bh.GetBoxInfoByID(id);
        if(t != null) {
            return gs.toJson(t);
        }
        return null;
    }
}

