package dk.nielshvid.intermediary.Test;

import dk.nielshvid.intermediary.CapabilityHandler;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.UUID;

public class TestCapabilityHandler {
    private CapabilityHandler capabilityHandler;

    @BeforeMethod
    public void setup() {
        HashMap<String, CapabilityHandler.Node<String>> treeTemplates = new HashMap<String, CapabilityHandler.Node<String>>(){{
            put("BoxDB/get", new CapabilityHandler.Node<String>("BoxDB/get"){{
                addChild(new CapabilityHandler.Node<String>("Freezer/retrieve"){{
                    addChild(new CapabilityHandler.Node<String>("BoxDB/retrieve"){{
                    }});
                }});
            }});
            put("BoxDB/findEmptySlot", new CapabilityHandler.Node<String>("BoxDB/findEmptySlot"){{
                addChild(new CapabilityHandler.Node<String>("Freezer/insert"){{
                    addChild(new CapabilityHandler.Node<String>("BoxDB/insert"){{
                    }});
                }});
            }});
        }};
        capabilityHandler = new CapabilityHandler(treeTemplates);
    }

    @Test
    public void addingCapabilityWithValidKeyReturnsID() {
        UUID ID = capabilityHandler.addCapability("","BoxDB/get");
        Assert.assertEquals(ID.getClass(), UUID.randomUUID().getClass());
    }

    @Test
    public void addingCapabilityWithInvalidKeyReturnsNull() {
        UUID ID = capabilityHandler.addCapability("","JohnSnow");
        Assert.assertNull(ID);
    }

    @Test
    public void validCapabilityReturnsTrueWhenUsed() {
        UUID ID = capabilityHandler.addCapability("niels","BoxDB/get");
        boolean actual = capabilityHandler.authorize("niels", ID, "Freezer/retrieve");
        Assert.assertTrue(actual);
    }

    @Test
    public void invalidActionReturnsFalseWhenUsed() {
        UUID ID = capabilityHandler.addCapability("niels","BoxDB/get");
        boolean actual = capabilityHandler.authorize("InvalidUserID", ID, "Freezer/insert");
        Assert.assertFalse(actual);
    }

    @Test
    public void invalidUserIDReturnsFalseWhenUsed() {
        UUID ID = capabilityHandler.addCapability("niels","BoxDB/get");
        boolean actual = capabilityHandler.authorize("InvalidUserID", ID, "Freezer/retrieve");
        Assert.assertFalse(actual);
    }

    @Test
    public void doubleValiedCapabilityAuthorizeTestReturnsTrue() {
        UUID ID = capabilityHandler.addCapability("niels","BoxDB/get");
        capabilityHandler.authorize("niels", ID, "Freezer/retrieve");
        boolean actual = capabilityHandler.authorize("niels", ID, "BoxDB/retrieve");
        Assert.assertTrue(actual);
    }

    @Test
    public void doubleInvaliedCapabilityAuthorizeTestReturnsTrue() {
        UUID ID = capabilityHandler.addCapability("niels","BoxDB/get");
        capabilityHandler.authorize("niels", ID, "Freezer/retrieve");
        boolean actual = capabilityHandler.authorize("InvalidUserID", ID, "BoxDB/retrieve");
        Assert.assertFalse(actual);
    }

    @Test
    public void doubleSpendingReturnsFalse() {
        UUID ID = capabilityHandler.addCapability("niels","BoxDB/get");
        capabilityHandler.authorize("niels", ID, "Freezer/retrieve");
        capabilityHandler.authorize("niels", ID, "BoxDB/retrieve");
        boolean actual = capabilityHandler.authorize("niels", ID, "BoxDB/retrieve");
        Assert.assertFalse(actual);
    }
}
