import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class Box{

    Box(int x, int y, int width, int height ) {
        // Rectangle
        rectangle = new Rectangle(x,y,width,height);
        rectangle.setFill(Color.rgb(120,120,120));
        rectangle.setStrokeWidth(1);
        rectangle.setStroke(Color.rgb(0,0,0));
        rectangle.setStrokeType(StrokeType.CENTERED);
        rectangle.setSmooth(false);

        // Green light
        green = new Circle(x + 20, y + 20, 5);
        green.setFill(Color.rgb(0,40,0));
        green.setStroke(Color.rgb(0,0,0));
        green.setStrokeWidth(1);

        // Red light
        red = new Circle(x + 40, y + 20, 5);
        red.setFill(Color.rgb(255,0,0));
        red.setStroke(Color.rgb(0,0,0));
        red.setStrokeWidth(1);

        group = new Group(rectangle);
        group.getChildren().add(green);
        group.getChildren().add(red);
    }

    public void open() {
        rectangle.setFill(Color.rgb(140,140,140));

        green.setFill(Color.rgb(0,200,0));
        red.setFill(Color.rgb(100,0,0));

        open = true;
    }

    public void close(){
        rectangle.setFill(Color.rgb(120,120,120));

        green.setFill(Color.rgb(0,40,0));
        red.setFill(Color.rgb(255,0,0));

        open = false;
    }

    public boolean get(){
        return open;
    }

    Group group;

    private Rectangle rectangle;
    private Circle green;
    private Circle red;
    private boolean open = false;
}
