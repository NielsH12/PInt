import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.List;

public class FFUGUI extends Application implements Runnable{

    private static Box[][] boxes;
    private static List<Pos> empty;

    public FFUGUI(Box[][] _boxes, List<Pos> _empty){
        boxes = _boxes;
        empty = _empty;
    }

    public FFUGUI(){

    }

    // Open a storage slot
    public void open(Pos pos){
        // TODO: AUTOCLOSE AFTER TIMEOUT

        empty.add(pos);
        boxes[pos.x][pos.y].open();
    }

    // Close storage slot (no contents)
    public void close(Pos pos){
        boxes[pos.x][pos.y].close("");
    }

    // Store contents at some empty storage slot
    public Pos store(String id){
        if (!empty.isEmpty()){
            Pos pos = empty.remove(0);

            boxes[pos.x][pos.y].close(id);
            return pos;
        }

        return new Pos(-1,-1); // TODO: Maybe replace with throwing an error
    }

    // GUI stuff
    @Override
    public void start(Stage primaryStage) throws Exception{
        Group root = new Group();

        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 3; j++){
                root.getChildren().add(boxes[i][j].group);
            }
        }

        Scene scene = new Scene(root,600,300);
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                int row = ((int) Math.floor(event.getSceneX() / 150));
                int col = ((int) Math.floor(event.getSceneY() / 100));

                Pos t = new Pos(row,col);

                if(boxes[t.x][t.y].open) {
                    close(t);
                } else {
                    open(t);
                }
            }
        });

        primaryStage.setTitle("FFU - Automation Layer");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("https://www.contegix.com/wp-content/uploads/2017/11/snow-cold-flake-snowfall-snowflake-weather-388d22cfbc51ea26-512x512.png"));
        primaryStage.show();
    }

    public void run() { launch(); }
}
