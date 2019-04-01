package dk.nielshvid.intermediator;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TestHandler {



    public static void main(String[] arg) {

        UUID test = UUID.randomUUID();

        String BoxID = "69E16635-F76F-403C-A231-18E666F04FC2@1E3C9DBF-C004-4F93-B3BB-D1E45945D482";
        UUID Jens = UUID.fromString("B6F64D8F-1916-4236-9BBA-039A380329AD");


        IdentityService identityService = new IdentityService(){
            @Override
            public String getRole(UUID UserID, String BoxID){
                return "Doctor";
            }
        };

        Guard guard = new Guard(identityService);

        List<String> fisk = new ArrayList<>();
        fisk.add("3");

        List<String> y = new ArrayList<>();
        y.add("2");

        MultivaluedMap<String, String> queryParamMap = new MultivaluedHashMap<String, String>(){{
            put("xPos", fisk);
            put("yPos", y);
        }};

        System.out.println(guard.authorize(Jens.toString(), BoxID, null, "fiskeLars", queryParamMap));


    }
}




//        UUID Jens = UUID.fromString("B6F64D8F-1916-4236-9BBA-039A380329AD");
//        UUID Niels = UUID.fromString("79B3B2CE-6BEF-4D13-B13F-68D85A84909F");
//        UUID Hans = UUID.fromString("81998EE8-3A98-11E9-B210-D663BD873D93");
//        UUID SDU = UUID.fromString("24EF490F-B88A-4CEC-AD77-988B2074B2D6");
//        UUID TEK = UUID.fromString("1E3C9DBF-C004-4F93-B3BB-D1E45945D482");
//        UUID NAT = UUID.fromString("1841B69A-8F42-4E92-A8E0-DAB10D542DD5");
//
//
//        IdentityService test = new IdentityService();
//
//        String result = test.getRole(Hans, TEK);
//
//        System.out.println(result);


