import java.util.ArrayList;
import java.util.List;

import static spark.Spark.stop;

public class Main {
    public static void main(String[] args) throws InterruptedException{

        Box[][] boxes = new Box[4][3];
        List<Pos> empty = new ArrayList<>();

        // Initialize freezer with list of empty storage slots
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                boxes[i][j] = new Box(150 * i, 100 * j, 150,100);
                empty.add(new Pos(i,j));
            }
        }

        Thread gui = new Thread(new FFUGUI(boxes, empty));
        gui.start();

        Thread rest = new Thread(new FFUREST(boxes, empty));
        rest.start();

        while(gui.isAlive()) {
                Thread.sleep(1000);
            }

        stop();
    }
}
