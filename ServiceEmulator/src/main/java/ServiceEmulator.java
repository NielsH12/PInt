import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServiceEmulator {

    static RestClient RC;
    static BufferedReader br;
    static String input;

    private static void insert() throws IOException{
        System.out.println("Enter ID of the box to insert");
        input = br.readLine();

        Response r = RC.insertBoxByID(input);
    }

    private static void get() throws IOException{
        System.out.println("Enter ID of the box");
        input = br.readLine();

        Response r = RC.getBoxInfoByID(input);
    }

    private static void open() throws IOException{
        System.out.println("Enter ID of the box to retrieve");
        input = br.readLine();

        Response r = RC.openBoxByID(input);
    }

    public static void main(String[] args) throws IOException {
        br = new BufferedReader(new InputStreamReader(System.in));
        RC = new RestClient();

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
