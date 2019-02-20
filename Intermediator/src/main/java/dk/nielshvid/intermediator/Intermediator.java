package dk.nielshvid.intermediator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;

import dk.nielshvid.storagemanagement.*;

public class Intermediator {

    private static Gson gs = new Gson();

    static BoxHandler bh = new BoxHandler();

    static String lookup(String id){
        System.out.println("Shit son, I'm asked to look up a box. Hang on");
        dbBox t = bh.GetBoxInfoByID(id);
        System.out.println("I'M BACK BABY");
        String json = gs.toJson(t);
        System.out.println(json);
        return json;
    }
}

