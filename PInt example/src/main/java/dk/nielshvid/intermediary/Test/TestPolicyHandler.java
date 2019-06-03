package dk.nielshvid.intermediary.Test;

import dk.nielshvid.intermediary.Entities;
import dk.nielshvid.intermediary.InformationServiceInterface;
import dk.nielshvid.intermediary.PolicyHandler;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.MultivaluedMap;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestPolicyHandler {
    private PolicyHandler policyHandler;
    private TestInformationServiceInterface informationService;
    private MultivaluedMap<String, String> map;
    private String SAMPLE = "sample";

    @BeforeMethod
    public void setup() {
        map = mock(MultivaluedMap.class);


        //TODO: fix rolePolicyMap
        policyHandler = new PolicyHandler(rolePolicyMap, entityPolicyMap, informationService);
    }

    // RoleAuthorize
    @Test
    public void Role_Without_ActionPermission_ReturnsFlase() {
        boolean actual = policyHandler.roleAuthorize("Decan", "Freezer/retrieve", null, null);
        Assert.assertFalse(actual);

        actual = policyHandler.roleAuthorize("Doctor", "ActionNotAllowed", null, null);
        Assert.assertFalse(actual);
    }

    @Test
    public void Role_with_ActionPermission_returnsTrue() {
        final boolean actual = policyHandler.roleAuthorize("Doctor", "Freezer/retrieve", null, null);
        Assert.assertTrue(actual);
    }

    @Test
    public void Role_with_ActionPermission_And_MapWithAllowedQParametors_returnsTrue() {
        when(map.getFirst("xPos")).thenReturn("2");
        when(map.getFirst("yPos")).thenReturn("0");

        final boolean actual = policyHandler.roleAuthorize("Doctor", "BoxDB/insert", map, null);
        Assert.assertTrue(actual);
    }

    @Test
    public void Role_with_ActionPermission_And_MapWithProhibitedQParametors_returnsFalse() {
        String prohibitedXValue = "3";
        String prohibitedYValue = "1";
        String allowedXValue = "2";
        String allowedYValue = "0";

        Assert.assertFalse(setupQmap(prohibitedXValue, allowedYValue));
        Assert.assertFalse(setupQmap(allowedXValue, prohibitedYValue));
        Assert.assertFalse(setupQmap(prohibitedXValue, prohibitedYValue));
    }

    // EntityAuthorize
    @Test
    public void EntityAuthorize_WithNull_ReturnsFalse() {
        boolean actual = policyHandler.entityAuthorize(null, null, null);
        Assert.assertFalse(actual);

        actual = policyHandler.entityAuthorize(SAMPLE, null, null);
        Assert.assertFalse(actual);

        actual = policyHandler.entityAuthorize(null, "Freezer/insert", null);
        Assert.assertFalse(actual);
    }

    @Test
    public void EntityAuthorize_WithAllwaysTrueExp_ReturnsTrue() {
        // allways true boolean exp
        boolean actual = policyHandler.entityAuthorize(SAMPLE, "Freezer/insert", map);
        Assert.assertTrue(actual);
    }

    @Test
    public void EntityAuthorize_WithAllowedQPmap_ReturnsTrue() {
        int correctAnswer = 0;

        boolean actual = testEntityAuthorizeWithInformationService(correctAnswer);
        Assert.assertTrue(actual);
    }

    @Test
    public void EntityAuthorize_WithProhibitedQPmap_ReturnsFalse() {
        int wrongAnswer = 2;

        boolean actual = testEntityAuthorizeWithInformationService(wrongAnswer);
        Assert.assertFalse(actual);
    }

    @Test
    public void EntityAuthorize_WithLocalDateQPmap_AreEnforced() {
        LocalDate localDate = LocalDate.now().minusDays(5);

        String uuid = UUID.randomUUID().toString();
        when(map.getFirst("SampleID")).thenReturn(uuid);

        TestSample sample = mock(TestSample.class);
        sample.accessed = localDate;

        informationService = mock(TestInformationServiceInterface.class);
        when(informationService.TestgetSample(uuid)).thenReturn(sample);

        // True exp
        boolean actual = policyHandler.entityAuthorize(SAMPLE, "TestDateCompare", map);
        Assert.assertTrue(actual);

        // False exp
        sample.accessed = LocalDate.now();
        actual = policyHandler.entityAuthorize(SAMPLE, "TestDateCompare", map);
        Assert.assertFalse(actual);
    }

    private boolean testEntityAuthorizeWithInformationService(int wrongAnswer) {
        String uuid = UUID.randomUUID().toString();
        when(map.getFirst("SampleID")).thenReturn(uuid);

        TestSample sample = mock(TestSample.class);
        sample.temperature = wrongAnswer;

        informationService = mock(TestInformationServiceInterface.class);
        when(informationService.TestgetSample(uuid)).thenReturn(sample);

        return policyHandler.entityAuthorize(SAMPLE, "Freezer/retrieve", map);
    }

    private boolean setupQmap(String xPos, String yPos){
        when(map.getFirst("xPos")).thenReturn(xPos);
        when(map.getFirst("yPos")).thenReturn(yPos);
        return policyHandler.roleAuthorize("Doctor", "BoxDB/insert", map, null);
    }

    private HashMap<String, HashMap<String, PolicyHandler.RoleCondition>> rolePolicyMap = new HashMap<String, HashMap<String, PolicyHandler.RoleCondition>>() {{
        put("Decan", new HashMap<String, PolicyHandler.RoleCondition>(){{
        }});
        put("Doctor", new HashMap<String, PolicyHandler.RoleCondition>(){{
            put("Freezer/retrieve", (map, body) -> true);
            put("Freezer/insert", (map, body) -> true);
            put("BoxDB/retrieve", (map, body) -> (1 <= 2));
            put("BoxDB/insert", (map, body) -> ((Integer.parseInt(map.getFirst("xPos")) == 2)&&(Integer.parseInt(map.getFirst("yPos")) == 0)));
        }});
        put("Assistant", new HashMap<String, PolicyHandler.RoleCondition>(){{
        }});
        put("Student", new HashMap<String, PolicyHandler.RoleCondition>(){{
        }});
    }};

    private HashMap<String, HashMap<String, PolicyHandler.EntityCondition>> entityPolicyMap = new HashMap<String, HashMap<String, PolicyHandler.EntityCondition>>() {{
        put(SAMPLE, new HashMap<String, PolicyHandler.EntityCondition>(){{
        }});
        put(SAMPLE, new HashMap<String, PolicyHandler.EntityCondition>(){{
            put("Freezer/insert", (map) -> (2 == 2));
            put("Freezer/retrieve", (map) -> (informationService.TestgetSample(map.getFirst("SampleID")).temperature == 0));
            put("TestDateCompare", (map) -> (1 < PolicyHandler.CompareDates(informationService.TestgetSample(map.getFirst("SampleID")).accessed, LocalDate.now())));
        }});
    }};

    private interface TestInformationServiceInterface extends InformationServiceInterface{

        String getRoleByEntity(String UserID, String EntityID);
        String getRoleByOrganization(String UserID, String OrganizationID);
        String getEntityType(MultivaluedMap<String, String> QPmap);

        TestSample TestgetSample(String id);
    }

    public static class TestSample {
        public LocalDate accessed;
        public int temperature;
    }
}

