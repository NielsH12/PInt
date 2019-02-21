import com.google.gson.Gson;
import dk.nielshvid.storagemanagement.dbBox;

import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;


//A70D717E-935E-4CA2-8192-22E65D84BF71
public class ServiceEmulator {

    private static BufferedReader br;
    private static String input;
    private static Gson gs = new Gson();

    private static void insert() throws IOException{
        String input ="";

        System.out.println("Enter ID of the box to insert");
        input = br.readLine();

        if (!verifyID(input)){
            return;
        }

        Response r = RestClient.insertBoxByID(input);

        if(r.getStatus() != 200){
            System.out.println(r.getStatus());
            System.out.println(r.getStatusInfo().getReasonPhrase());
            return;
        }
    }

    private static void get() throws IOException{
        System.out.println("Enter ID of the box");
        input = br.readLine();

        if (!verifyID(input)){
            System.out.println("Not valid id");
            return;
        }

        Response r = RestClient.getBoxInfoByID(input);

        if(r.getStatus() != 200){
            System.out.println(r.getStatus());
            System.out.println(r.getStatusInfo().getReasonPhrase());
            return;
        }

        try {
            dbBox t = gs.fromJson(r.readEntity(String.class), dbBox.class);
            System.out.println(t.firstName);
        } catch (Exception e){
            System.out.println("Response could not be converted to dbBox.class");
        }
    }

    private static void open() throws IOException{
        System.out.println("Enter ID of the box to retrieve");
        input = br.readLine();

        if (!verifyID(input)){
            System.out.println("Not valid id");
            return;
        }

        Response r = RestClient.openBoxByID(input);

        if(r.getStatus() != 200){
            System.out.println(r.getStatus());
            System.out.println(r.getStatusInfo().getReasonPhrase());
        }
    }

    private static boolean verifyID(String id){
        try{
            UUID uuid = UUID.fromString(input);
            return true;
        } catch (IllegalArgumentException exception){
            return false;
        }
    }

    public static void main(String[] args) throws IOException {
        br = new BufferedReader(new InputStreamReader(System.in));

        while (true){
            System.out.println("Enter command (get, open, insert)");
            input = br.readLine();

            switch(input){
                case "get":
                    get();
                    break;

                case "open":
                    open();
                    break;

                case "insert":
                    insert();
                    break;

                default:
                    System.out.println("Invalid command");
                    break;
            }
            System.out.println();
        }
    }
}