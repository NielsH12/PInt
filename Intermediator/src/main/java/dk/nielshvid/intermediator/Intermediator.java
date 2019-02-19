package dk.nielshvid.intermediator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;

import dk.nielshvid.storagemanagement.*;

public class Intermediator {

    private static Gson gs = new Gson();

    static String lookup(String id){
        dbBox t = BoxHandler.getInstance().GetBoxInfoByID(id);
        String json = gs.toJson(t);
        return json;
    }
}

