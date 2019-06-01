package dk.nielshvid.intermediary.Test;

import dk.nielshvid.intermediary.*;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


// Frameworks:
// TestNG: https://testng.org/doc/
// Mockito: https://site.mockito.org/

public class TestShield {
    private Shield shield;
    private static String KeyInRolePolicyFreeActions = "KeyInRolePolicyFreeActions";
    private static String ENTITY_TYPES_WITH_NOTHING_FOR_TESTING = "ENTITY_TYPES_WITH_NOTHING_FOR_TESTING";
    private static String keyNotInRolePolicyFreeActions = "keyNotInRolePolicyFreeActions";
    private static String RoleWithPolicy = "RoleWithPolicy";
    private static String RoleWithOutAuthorization = "RoleWithOutAuthorization";
    private static String ImplementedAction = "ImplementedAction";
    private static String ImplementedActionWithBoolExp = "ImplementedActionWithBoolExp";
    private static String ImplementedActionWithFalseBoolExp = "ImplementedActionWithFalseBoolExp";
    private static String ActionWithEntityPolicy = "ActionWithEntityPolicy";
    private static String EntityWithNoPolicies = "EntityWithNoPolicies";
    private static String ActionEnforcedByCapOnly = "ActionEnforcedByCapOnly";
    private TestInformationServiceInterface informationService;
    private static UUID expectedUUID = UUID.randomUUID();
    private static UUID ValidCapaID = UUID.randomUUID();
    private static String UserID = "userID";
    private static String UserIDWithOutAccess = "UserIDWithOutAccess";
    private static String EntityID = "entityID";
    private static String SAMPLE = "SAMPLE";
    private MultivaluedMap<String, String> QPmap = new MultivaluedStringMap();
    private MultivaluedMap<String, String> mocKQPmap = new MultivaluedStringMap();
    MultivaluedMap<String, String> QPmapWithAllowedRole = new MultivaluedStringMap();
    MultivaluedMap<String, String> QPmapWithDeniedRole = new MultivaluedStringMap();

    @BeforeMethod
    public void setUp() {
        informationService = mock(TestInformationServiceInterface.class);
        when(informationService.getRoleByEntity(UserID, EntityID)).thenReturn(RoleWithPolicy);
        when(informationService.getRoleByEntity(UserIDWithOutAccess, EntityID)).thenReturn(RoleWithOutAuthorization);
        QPmap.add("EntityID", EntityWithNoPolicies);
        when(informationService.getEntityType(any())).thenReturn(SAMPLE);

        QPmapWithAllowedRole.add("UserID", UserID);
        QPmapWithAllowedRole.add("EntityID", EntityID);

        QPmapWithDeniedRole.add("UserID", UserIDWithOutAccess);
        QPmapWithDeniedRole.add("EntityID", EntityID);

        PolicyHandler PH = new PolicyHandler(rolePolicyMap, entityPolicyMap, informationService);

        CapabilityHandler CH = mock(CapabilityHandler.class);
        when(CH.addCapability(anyString(), anyString())).thenReturn(expectedUUID);
        when(CH.authorize(UserID, ValidCapaID, ActionEnforcedByCapOnly)).thenReturn(true);

        shield = new Shield(informationService, rolePolicyFreeActions, capabilityRequiringResources, entityPolicyRequiringActions, PH, CH);
    }

    //rolePolicyFreeActions
    @Test
    public void Action_in_rolePolicyFreeActions_ReturnsTrue() {
        final boolean actual = shield.authorize(KeyInRolePolicyFreeActions, mocKQPmap, null);
        Assert.assertTrue(actual);
    }

    @Test(expectedExceptions = {WebApplicationException.class}, expectedExceptionsMessageRegExp = "RP: Permission denied")
    public void Action_notIn_rolePolicyFreeActions_ReturnsException() {
        when(informationService.getRoleByEntity(UserID, EntityID)).thenReturn(RoleWithOutAuthorization);

        shield.authorize(keyNotInRolePolicyFreeActions, mocKQPmap, null);
    }

    //rolePolicies
    @Test
    public void User_with_the_Right_role_AreAuthorized() {
        Assert.assertTrue(testShieldRoleAuthorize(ImplementedAction, QPmapWithAllowedRole));
    }

    @Test(expectedExceptions = {WebApplicationException.class}, expectedExceptionsMessageRegExp = "RP: Permission denied")
    public void User_with_the_Wrong_role_ReturnsException() {
        testShieldRoleAuthorize(ImplementedAction, QPmapWithDeniedRole);
    }

    @Test
    public void User_with_the_Right_role_And_trueBooleanExp_AreAuthorized() {
        Assert.assertTrue(testShieldRoleAuthorize(ImplementedActionWithBoolExp, QPmapWithAllowedRole));
    }

    @Test(expectedExceptions = {WebApplicationException.class}, expectedExceptionsMessageRegExp = "RP: Permission denied")
    public void User_with_the_Right_role_And_falseBooleanExp_ReturnsException() {
        Assert.assertTrue(testShieldRoleAuthorize(ImplementedActionWithFalseBoolExp, QPmapWithAllowedRole));
    }

    //entityPolicy
    @Test
    public void User_with_the_Right_role_And_trueEntityExp_AreAuthorized() {
        Assert.assertTrue(testShieldEntityAuthorize(ActionWithEntityPolicy, QPmapWithAllowedRole, -1));
    }

    @Test(expectedExceptions = {WebApplicationException.class}, expectedExceptionsMessageRegExp = "EP: Permission denied")
    public void User_with_the_Right_role_And_falseEntityExp_AreNoRAuthorized() {

       testShieldEntityAuthorize(ActionWithEntityPolicy, QPmapWithAllowedRole, 1);
    }

    // CapabilityPolicies
    @Test
    public void GenerateCapability_returns_UUID_WhenNoPolicyIsBlocking() {
        UUID actual = shield.generateCapability(KeyInRolePolicyFreeActions, QPmapWithAllowedRole,null, false);
        Assert.assertEquals(actual, expectedUUID);
    }

    @Test
    public void GenerateCapability_Are_Enforced_By_Policies() {
        // fail rolePolicy
        UUID expectedNull = shield.generateCapability(keyNotInRolePolicyFreeActions, QPmapWithAllowedRole, null, false);
        Assert.assertNull(expectedNull);

        // pass rolePolicy
        UUID actual = shield.generateCapability(ImplementedActionWithBoolExp, QPmapWithAllowedRole, null, false);
        Assert.assertEquals(actual, expectedUUID);
    }

    @Test(expectedExceptions = {WebApplicationException.class}, expectedExceptionsMessageRegExp = "CP: Missing input")
    public void capabilityPoliciesAreEnforced_WithWrongCapabilityID_ThrowException() {
        MultivaluedMap<String, String> localMap = new MultivaluedStringMap();
        localMap.add("UserID", UserID);
        localMap.add("EntityID", EntityID);

        shield.authorize(ActionEnforcedByCapOnly, localMap, null);
    }

    @Test
    public void capabilityPoliciesAreEnforced_WithRightCapabilityID_ReturnsTrue() {
        MultivaluedMap<String, String> localMap = new MultivaluedStringMap();
        localMap.add("UserID", UserID);
        localMap.add("EntityID", EntityID);
        localMap.add("CapabilityID", String.valueOf(ValidCapaID));
        final boolean actual = shield.authorize(ActionEnforcedByCapOnly, localMap, null);

        Assert.assertTrue(actual);
    }


    private boolean testShieldRoleAuthorize(String implementedAction, MultivaluedMap<String, String> _mocKQPmap) {
        when(informationService.TestgetEntityType(EntityID)).thenReturn(SAMPLE);

        return shield.authorize(implementedAction, _mocKQPmap, null);
    }

    private boolean testShieldEntityAuthorize(String implementedAction, MultivaluedMap<String, String> _mocKQPmap, int temperature) {

        when(informationService.TestgetEntityType(EntityID)).thenReturn(SAMPLE);

        TestSample sample = new TestSample();
        sample.temperature = temperature;

        _mocKQPmap.add("ID", "someid");

        when(informationService.TestgetSample("someid")).thenReturn(sample);

        return shield.authorize(implementedAction, _mocKQPmap, null);
    }

    private HashSet<String> rolePolicyFreeActions = new HashSet<String>() {{
        add(KeyInRolePolicyFreeActions);
        add(ActionEnforcedByCapOnly);
    }};

    private HashMap<String, HashMap<String, PolicyHandler.RoleCondition>> rolePolicyMap = new HashMap<String, HashMap<String, PolicyHandler.RoleCondition>>() {{
        put(RoleWithPolicy, new HashMap<String, PolicyHandler.RoleCondition>() {{
            put(ImplementedAction, (map, body) -> true);
            put(ActionWithEntityPolicy, (map, body) -> true);
            put(ImplementedActionWithBoolExp, (map, body) -> 1 <= 2);
            put(ImplementedActionWithFalseBoolExp, (map, body) -> 1 >= 2);
        }});
        put(RoleWithOutAuthorization, new HashMap<String, PolicyHandler.RoleCondition>());
    }};

    private HashMap<String, HashSet> entityPolicyRequiringActions = new HashMap<String, HashSet>(){{
        put(SAMPLE, new HashSet<String>(){{
            add(ActionWithEntityPolicy);
        }});
        put(ENTITY_TYPES_WITH_NOTHING_FOR_TESTING, new HashSet<String>(){{}});
    }};

    private HashMap<String, HashMap<String, PolicyHandler.EntityCondition>> entityPolicyMap = new HashMap<String, HashMap<String, PolicyHandler.EntityCondition>>() {{
        put(SAMPLE, new HashMap<String, PolicyHandler.EntityCondition>(){{
            put(ActionWithEntityPolicy, (map) -> (informationService.TestgetSample(map.getFirst("ID")).temperature < 0));
        }});
    }};

    private static HashSet<String> capabilityRequiringResources = new HashSet<String>() {{
        add("ActionEnforcedByCapOnly");
    }};

    private interface TestInformationServiceInterface extends InformationServiceInterface{
        TestSample TestgetSample(String ID);
        String TestgetEntityType(String ID);
    }

    public static class TestSample {
        int temperature;
    }
}
