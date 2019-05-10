package dk.nielshvid.intermediary.Test;

import dk.nielshvid.intermediary.CapabilityHandler;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

public class TestCapabilityHandler {
    //TODO: Niels do your magic
    private CapabilityHandler capabilityHandler;

    @BeforeMethod
    public void setup() {
        capabilityHandler = new CapabilityHandler("fisketest");
    }

    @Test
    public void addingCapabilityWithValidKeyReturnsID() {
        UUID ID = capabilityHandler.addCapability("","","BoxDB/get");
        Assert.assertEquals(ID.getClass(), UUID.randomUUID().getClass());
    }

    @Test
    public void addingCapabilityWithInvalidKeyReturnsNull() {
        UUID ID = capabilityHandler.addCapability("","","JohnSnow");
        Assert.assertNull(ID);
    }

    @Test
    public void validCapabilityReturnsTrueWhenUsed() {
        UUID ID = capabilityHandler.addCapability("niels","Sample","BoxDB/get");
        boolean actual = capabilityHandler.authorize("niels", "Sample", ID, "Freezer/retrieve");
        Assert.assertTrue(actual);
    }

    @Test
    public void invalidActionReturnsFalseWhenUsed() {
        UUID ID = capabilityHandler.addCapability("niels","Sample","BoxDB/get");
        boolean actual = capabilityHandler.authorize("InvalidUserID", "Sample", ID, "Freezer/insert");
        Assert.assertFalse(actual);
    }

    @Test
    public void invalidUserIDReturnsFalseWhenUsed() {
        UUID ID = capabilityHandler.addCapability("niels","Sample","BoxDB/get");
        boolean actual = capabilityHandler.authorize("InvalidUserID", "Sample", ID, "Freezer/retrieve");
        Assert.assertFalse(actual);
    }

    @Test
    public void invalidEntityIDReturnsFalseWhenUsed() {
        UUID ID = capabilityHandler.addCapability("niels","Sample","BoxDB/get");
        boolean actual = capabilityHandler.authorize("niels", "InvalidEntiyID", ID, "Freezer/retrieve");
        Assert.assertFalse(actual);
    }

    @Test
    public void doubleValiedCapabilityAuthorizeTestReturnsTrue() {
        UUID ID = capabilityHandler.addCapability("niels","Sample","BoxDB/get");
        capabilityHandler.authorize("niels", "Sample", ID, "Freezer/retrieve");
        boolean actual = capabilityHandler.authorize("niels", "Sample", ID, "BoxDB/retrieve");
        Assert.assertTrue(actual);
    }

    @Test
    public void doubleInvaliedCapabilityAuthorizeTestReturnsTrue() {
        UUID ID = capabilityHandler.addCapability("niels","Sample","BoxDB/get");
        capabilityHandler.authorize("niels", "Sample", ID, "Freezer/retrieve");
        boolean actual = capabilityHandler.authorize("InvalidUserID", "Sample", ID, "BoxDB/retrieve");
        Assert.assertFalse(actual);
    }

    @Test
    public void doubleSpendingReturnsFalse() {
        UUID ID = capabilityHandler.addCapability("niels","Sample","BoxDB/get");
        capabilityHandler.authorize("niels", "Sample", ID, "Freezer/retrieve");
        capabilityHandler.authorize("niels", "Sample", ID, "BoxDB/retrieve");
        boolean actual = capabilityHandler.authorize("niels", "Sample", ID, "BoxDB/retrieve");
        Assert.assertFalse(actual);
    }
}
