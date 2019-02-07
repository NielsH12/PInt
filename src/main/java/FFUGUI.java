import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;


public class FFUGUI extends Application implements Runnable{

    static Box[][] boxes = new Box[4][3];
    boolean open = false;

    public FFUGUI(){

    }

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

                if(boxes[row][col].get()) {
                    boxes[row][col].close("");

                } else {
                    boxes[row][col].open();
                }


            }
        });

        primaryStage.setTitle("FFU - Automation Layer");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("https://www.contegix.com/wp-content/uploads/2017/11/snow-cold-flake-snowfall-snowflake-weather-388d22cfbc51ea26-512x512.png"));
        primaryStage.show();

    }

    public void open(int row, int col){
        boxes[row][col].open();
    }

    public void close(int row, int col){
        boxes[row][col].close("");
    }

    public boolean get(int row, int col){
        return boxes[row][col].get();
    }

    public void run() {
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 3; j++){
                boxes[i][j] = new Box(150 * i, 100 * j, 150,100);
            }
        }

        launch();
    }
}
