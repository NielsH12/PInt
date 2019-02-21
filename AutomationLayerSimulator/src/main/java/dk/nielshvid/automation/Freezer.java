package dk.nielshvid.automation;

public class Freezer {
    int x;
    int y;
    String ID;

    public String htmlString(){

        if (ID != null && !ID.isEmpty()){
            return ID;
        } else {
            return "Empty";
        }
    }
}
