package dk.nielshvid.intermediator;

import java.util.HashMap;
import java.util.UUID;

public class TestHandler {

    public static void main(String[] arg){

        CapabilityHandler capabilityHandler = new CapabilityHandler();




//        capTree.put("get", new CapabilityHandler.Node<String>());

        UUID test = UUID.randomUUID();

        UUID capID = capabilityHandler.addCapability(test, "get");

        System.out.println(capabilityHandler.useAction(test, capID,"retrieve"));

        System.out.println(capabilityHandler.useAction(test, capID,"move"));

        System.out.println(capabilityHandler.useAction(test, capID,"testCase"));






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

    }
}
