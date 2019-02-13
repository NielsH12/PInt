import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class FFUREST implements Runnable{

    private static Box[][] boxes;// = new Box[4][3];
    private static List<Pos> empty;// = new ArrayList<>();

    public FFUREST(Box[][] _boxes, List<Pos> _empty){
        boxes = _boxes;
        empty = _empty;
    }



    public void stop(){
        stop();
    }

    public void run(){
        get("/hello", (request, response) -> "world");


        // Get
        get("/get/:row/:col", (request, response) -> {

            Map<String, String> args = request.params();

            if (args.containsKey(":row") && args.containsKey(":col")){
                int row = Integer.parseInt(args.get(":row"));
                int col = Integer.parseInt(args.get(":col"));

                    return boxes[row][col].isEmpty;
            }

            return "?";
        });

        // Open
        get("/open/:row/:col", (request, response) -> {

            Map<String, String> args = request.params();

            if (args.containsKey(":row") && args.containsKey(":col")){
                int row = Integer.parseInt(args.get(":row"));
                int col= Integer.parseInt(args.get(":col"));

                Pos pos = new Pos(row, col);

                //gui.open(new Pos(row,col));
                synchronized(empty){
                    empty.add(pos);
                }
                boxes[pos.x][pos.y].open();
            }

            return "success";
        });

        // Print empty
        get("/print", (request, response) -> {

            empty.forEach((n) -> n.print());
            return "ok";
        });

        // Close
        get("/close/:row/:col", (request, response) -> {

            Map<String, String> args = request.params();

            if (args.containsKey(":row") && args.containsKey(":col")){
                int row = Integer.parseInt(args.get(":row"));
                int col= Integer.parseInt(args.get(":col"));

                boxes[row][col].close("");
            }

            return "success";
        });

        // Store
        get("/store/:id", (request, response) -> {

            Map<String, String> args = request.params();

            if (args.containsKey(":id")){
                String id = args.get(":id");

                synchronized (empty){
                    if (!empty.isEmpty()){
                        Pos pos = empty.remove(0);

                        boxes[pos.x][pos.y].close(id);
                        return pos.x + " " + pos.y;
                    }
                }


                return new Pos(-1,-1); // Maybe replace with throwing an error
            }

            return "failed";
        });
    }
}
