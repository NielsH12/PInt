import static spark.Spark.stop;

public class Main {
    public static void main(String[] args) throws InterruptedException{

        FFUGUI guiObj = new FFUGUI();

        Thread gui = new Thread(guiObj);
        gui.start();

        Thread rest = new Thread(new FFUREST(guiObj));
        rest.start();

        while(gui.isAlive()) {
                Thread.sleep(1000);
            }

        stop();

    }
}
