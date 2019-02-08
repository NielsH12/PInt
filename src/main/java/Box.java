import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Box{

    Box(int x, int y, int width, int height ) {
        // Rectangle
        rectangle = new Rectangle(x,y,width,height);
        rectangle.setFill(boxClosed);
        rectangle.setStrokeWidth(1);
        rectangle.setStroke(border);
        rectangle.setStrokeType(StrokeType.CENTERED);
        rectangle.setSmooth(false);

        // Green light
        green = new Circle(x + 20, y + 20, 5);
        green.setFill(greenCircleClosed);
        green.setStroke(border);
        green.setStrokeWidth(1);

        // Red light
        red = new Circle(x + 40, y + 20, 5);
        red.setFill(redCircleClosed);
        red.setStroke(border);
        red.setStrokeWidth(1);

        // Label
        label = new Text(x,y+50, "Empty");

        label.setTextAlignment(TextAlignment.CENTER);
        label.setWrappingWidth(150);
        label.setFont(Font.font("Verdana", FontPosture.ITALIC, 14));

        group = new Group(rectangle);
        group.getChildren().add(green);
        group.getChildren().add(red);
        group.getChildren().add(label);
    }

    public void open() {
        rectangle.setFill(boxOpen);

        green.setFill(greenCircleOpen);
        red.setFill(redCircleOpen);

        label.setText("");

        open = true;
        isEmpty = true;
        label.setFont(Font.font("Verdana", FontPosture.ITALIC, 14));
    }

    public void close(String _label){
        // Visual
        rectangle.setFill(boxClosed);
        green.setFill(greenCircleClosed);
        red.setFill(redCircleClosed);

        if (_label == ""){
            label.setText("Empty");
            isEmpty = true;
            label.setFont(Font.font("Verdana", FontPosture.ITALIC, 14));
        } else {
            label.setText(_label);
            isEmpty = false;
            label.setFont(Font.font("Verdana", FontPosture.REGULAR, 16));
        }

        open = false;
    }

    Group group;

    // Colors
    private Color boxClosed = new Color(0.47,0.47,0.47,1);
    private Color boxOpen = new Color(0.56,0.56,0.56,1);
    private Color redCircleClosed = new Color(1,0,0,1);
    private Color redCircleOpen = new Color(0.39,0,0,1);
    private Color greenCircleClosed = new Color(0,0.16,0,1);
    private Color greenCircleOpen = new Color(0,0.79,0,1);
    private Color border = new Color(0,0,0,1);



    // Shapes
    private Rectangle rectangle;
    private Circle green;
    private Circle red;

    public boolean open = false;
    public boolean isEmpty = true;
    private Text label;
}
