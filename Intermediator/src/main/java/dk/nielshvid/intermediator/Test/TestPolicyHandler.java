package dk.nielshvid.intermediator.Test;

import dk.nielshvid.intermediator.Entities;
import dk.nielshvid.intermediator.InformationServiceInterface;
import dk.nielshvid.intermediator.PolicyHandler;
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
    private InformationServiceInterface informationService;
    private MultivaluedMap<String, String> map;

    @BeforeMethod
    public void setUp() {
//        informationService = mock(InformationServiceInterface.class);
        map = mock(MultivaluedMap.class);
        policyHandler = new PolicyHandler(rolePolicyMap,entityPolicyMap, informationService);
    }

    // RoleAuthorize
    @Test
    public void Role_without_ActionPermission_returnsFlase() {
        boolean actual = policyHandler.roleAuthorize("Decan", "Freezer/retrieve", null);
        Assert.assertFalse(actual);

        actual = policyHandler.roleAuthorize("Doctor", "ActionNotAllowed", null);
        Assert.assertFalse(actual);
    }

    @Test
    public void Role_with_ActionPermission_returnsTrue() {
        final boolean actual = policyHandler.roleAuthorize("Doctor", "Freezer/retrieve", null);
        Assert.assertTrue(actual);
    }

    @Test
    public void Role_with_ActionPermission_And_MapWithAllowedQParametors_returnsTrue() {
        when(map.getFirst("xPos")).thenReturn("2");
        when(map.getFirst("yPos")).thenReturn("0");
        final boolean actual = policyHandler.roleAuthorize("Doctor", "BoxDB/insert", map);
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

        actual = policyHandler.entityAuthorize("Sample", null, null);
        Assert.assertFalse(actual);

        actual = policyHandler.entityAuthorize(null, "Freezer/insert", null);
        Assert.assertFalse(actual);
    }

    @Test
    public void EntityAuthorize_WithAllwaysTrueExp_ReturnsTrue() {
        // allways true boolean exp
        boolean actual = policyHandler.entityAuthorize("Sample", "Freezer/insert", map);
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

        Entities.Sample sample = mock(Entities.Sample.class);
        sample.accessed = localDate;

        informationService = mock(InformationServiceInterface.class);
        when(informationService.getSample(uuid)).thenReturn(sample);

        // True exp
        boolean actual = policyHandler.entityAuthorize("Sample", "TestDateCompare", map);
        Assert.assertTrue(actual);

        // False exp
        sample.accessed = LocalDate.now();
        actual = policyHandler.entityAuthorize("Sample", "TestDateCompare", map);
        Assert.assertFalse(actual);
    }

    private boolean testEntityAuthorizeWithInformationService(int wrongAnswer) {
        String uuid = UUID.randomUUID().toString();
        when(map.getFirst("SampleID")).thenReturn(uuid);

        Entities.Sample sample = mock(Entities.Sample.class);
        sample.temperature = wrongAnswer;

        informationService = mock(InformationServiceInterface.class);
        when(informationService.getSample(uuid)).thenReturn(sample);

        return policyHandler.entityAuthorize("Sample", "Freezer/retrieve", map);
    }

    private boolean setupQmap(String xPos, String yPos){
        when(map.getFirst("xPos")).thenReturn(xPos);
        when(map.getFirst("yPos")).thenReturn(yPos);
        return policyHandler.roleAuthorize("Doctor", "BoxDB/insert", map);
    }

    private HashMap<String, HashMap<String, PolicyHandler.Condition>> rolePolicyMap = new HashMap<String, HashMap<String, PolicyHandler.Condition>>() {{
        put("Decan", new HashMap<String, PolicyHandler.Condition>(){{
        }});
        put("Doctor", new HashMap<String, PolicyHandler.Condition>(){{
            put("Freezer/retrieve", map -> true);
            put("Freezer/insert", map -> true);
            put("BoxDB/retrieve", map -> (1 <= 2));
            put("BoxDB/insert", map -> ((Integer.parseInt(map.getFirst("xPos")) == 2)&&(Integer.parseInt(map.getFirst("yPos")) == 0)));
        }});
        put("Assistant", new HashMap<String, PolicyHandler.Condition>(){{
        }});
        put("Student", new HashMap<String, PolicyHandler.Condition>(){{
        }});
    }};

    private HashMap<String, HashMap<String, PolicyHandler.Condition>> entityPolicyMap = new HashMap<String, HashMap<String, PolicyHandler.Condition>>() {{
        put("Person", new HashMap<String, PolicyHandler.Condition>(){{
        }});
        put("Sample", new HashMap<String, PolicyHandler.Condition>(){{
            put("Freezer/insert", (map) -> (2 == 2));
            put("Freezer/retrieve", (map) -> (informationService.getSample(map.getFirst("SampleID")).temperature == 0));
            put("TestDateCompare", (map) -> (1 < PolicyHandler.CompareDates(informationService.getSample(map.getFirst("SampleID")).accessed, LocalDate.now())));
        }});
        put("Pizza", new HashMap<String, PolicyHandler.Condition>(){{
        }});
    }};
}

