package dk.nielshvid.automation;

public class Freezer {
    int x;
    int y;
    String ID;
    boolean Closed;

    public String htmlString(){
        if (Closed){
            if (ID != null && !ID.isEmpty()){
                return ID;

            } else {
                return "Empty";
            }
        } else {
            return "Open";
        }
    }
}
