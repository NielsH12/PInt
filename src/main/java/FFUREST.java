import java.util.Map;

import static spark.Spark.*;

public class FFUREST implements Runnable{

    public FFUREST(FFUGUI _gui){
        gui = _gui;
    }

    FFUGUI gui;

    public void stop(){
        stop();
        System.out.println("Stopping");
    }

    public void run(){
        get("/hello", (request, response) -> "world");

        get("/get/:row/:col", (request, response) -> {

            Map<String, String> args = request.params();

            if (args.containsKey(":row") && args.containsKey(":col")){
                int row = Integer.parseInt(args.get(":row"));
                int col= Integer.parseInt(args.get(":col"));

                return gui.get(row,col);
            }

            return "?";
        });



        get("/open/:row/:col", (request, response) -> {

            Map<String, String> args = request.params();

            if (args.containsKey(":row") && args.containsKey(":col")){
                int row = Integer.parseInt(args.get(":row"));
                int col= Integer.parseInt(args.get(":col"));

                gui.open(row,col);
            }

            return "success";
        });

        get("/close/:row/:col", (request, response) -> {

            Map<String, String> args = request.params();

            if (args.containsKey(":row") && args.containsKey(":col")){
                int row = Integer.parseInt(args.get(":row"));
                int col= Integer.parseInt(args.get(":col"));

                gui.close(row,col);
            }

            return "success";
        });



    }
}
