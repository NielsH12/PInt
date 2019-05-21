package dk.nielshvid.intermediary.Test;

import dk.nielshvid.intermediary.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static dk.nielshvid.intermediary.Entities.EntityType.ENTITY_TYPES_WITH_NOTHING_FOR_TESTING;
import static dk.nielshvid.intermediary.Entities.EntityType.SAMPLE;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


// Frameworks:
// TestNG: https://testng.org/doc/
// Mockito: https://site.mockito.org/

public class TestShield {
    private Shield shield;
    private static String KeyInRolePolicyFreeActions = "KeyInRolePolicyFreeActions";
    private static String keyNotInRolePolicyFreeActions = "keyNotInRolePolicyFreeActions";
    private static String RoleWithPolicy = "RoleWithPolicy";
    private static String ImplementedAction = "ImplementedAction";
    private static String ImplementedActionWithBoolExp = "ImplementedActionWithBoolExp";
    private static String ImplementedActionWithFalseBoolExp = "ImplementedActionWithFalseBoolExp";
    private static String ActionWithEntityPolicy = "ActionWithEntityPolicy";
    private static String EntityWithNoPolicies = "EntityWithNoPolicies";
    private static String ActionEnforcedByCapOnly = "ActionEnforcedByCapOnly";
    private InformationServiceInterface informationService;
    private static UUID expectedUUID = UUID.randomUUID();
    private static UUID ValidCapaID = UUID.randomUUID();
    private static String UserID = "userID";
    private static String EntityID = "entityID";

    @BeforeMethod
    public void setUp() {
        informationService = mock(InformationServiceInterface.class);
        when(informationService.getRole(UserID, EntityID)).thenReturn(RoleWithPolicy);
        when(informationService.getEntityType(EntityWithNoPolicies)).thenReturn(ENTITY_TYPES_WITH_NOTHING_FOR_TESTING);

        PolicyHandler PH = new PolicyHandler(rolePolicyMap, entityPolicyMap, informationService);

        CapabilityHandler CH = mock(CapabilityHandler.class);
        when(CH.addCapability(anyString(), anyString(), anyString())).thenReturn(expectedUUID);
        when(CH.authorize(UserID, EntityWithNoPolicies, ValidCapaID, ActionEnforcedByCapOnly)).thenReturn(true);

        shield = new Shield(informationService, rolePolicyFreeActions, capabilityRequiringActions, entityPolicyRequiringActions, PH, CH);
    }

    //rolePolicyFreeActions
    @Test
    public void Action_in_rolePolicyFreeActions_ReturnsTrue() {
        final boolean actual = shield.authorize(null, EntityWithNoPolicies, null, KeyInRolePolicyFreeActions, null);
        //
        Assert.assertTrue(actual);
    }

    @Test(expectedExceptions = {WebApplicationException.class}, expectedExceptionsMessageRegExp = "RP: Permission denied")
    public void Action_notIn_rolePolicyFreeActions_ReturnsException() {

        shield.authorize(null, null, null, keyNotInRolePolicyFreeActions, null);
    }

    //rolePolicies
    @Test
    public void User_with_the_Right_role_AreAuthorized() {
        Assert.assertTrue(testShieldRoleAuthorize(ImplementedAction, RoleWithPolicy));
    }

    @Test(expectedExceptions = {WebApplicationException.class}, expectedExceptionsMessageRegExp = "RP: Permission denied")
    public void User_with_the_Wrong_role_ReturnsException() {
        testShieldRoleAuthorize(ImplementedAction, "RoleWithNoPolicy");
    }

    @Test
    public void User_with_the_Right_role_And_trueBooleanExp_AreAuthorized() {
        Assert.assertTrue(testShieldRoleAuthorize(ImplementedActionWithBoolExp, RoleWithPolicy));
    }


    @Test(expectedExceptions = {WebApplicationException.class}, expectedExceptionsMessageRegExp = "RP: Permission denied")
    public void User_with_the_Right_role_And_falseBooleanExp_ReturnsException() {
        Assert.assertTrue(testShieldRoleAuthorize(ImplementedActionWithFalseBoolExp, RoleWithPolicy));
    }

    //entityPolicy
    @Test
    public void User_with_the_Right_role_And_trueEntityExp_AreAuthorized() {
        Assert.assertTrue(testShieldEntityAuthorize(ActionWithEntityPolicy, RoleWithPolicy, -1));
    }

    @Test(expectedExceptions = {WebApplicationException.class}, expectedExceptionsMessageRegExp = "EP: Permission denied")
    public void User_with_the_Right_role_And_falseEntityExp_AreNoRAuthorized() {
       testShieldEntityAuthorize(ActionWithEntityPolicy, RoleWithPolicy, 1);
    }

    // CapabilityPolicies
    @Test
    public void GenerateCapability_returns_UUID_WhenNoPolicyIsBlocking() {
        UUID actual = shield.generateCapability(UserID, EntityID, KeyInRolePolicyFreeActions, null);
        Assert.assertEquals(actual, expectedUUID);
    }

    @Test
    public void GenerateCapability_Are_Enforced_By_Policies() {
        // fail rolePolicy
        UUID expectedNull = shield.generateCapability(UserID, EntityID, keyNotInRolePolicyFreeActions, null);
        Assert.assertNull(expectedNull);

        // pass rolePolicy
        UUID actual = shield.generateCapability(UserID, EntityID, ImplementedActionWithBoolExp, null);
        Assert.assertEquals(actual, expectedUUID);
    }

    @Test(expectedExceptions = {WebApplicationException.class}, expectedExceptionsMessageRegExp = "CP: Invalid capability")
    public void capabilityPoliciesAreEnforced_WithWrongCapabilityID_ThrowException() {
        shield.authorize(UserID, EntityWithNoPolicies, null, ActionEnforcedByCapOnly, null);
    }

    @Test
    public void capabilityPoliciesAreEnforced_WithRightCapabilityID_ReturnsTrue() {
        final boolean actual = shield.authorize(UserID, EntityWithNoPolicies, ValidCapaID, ActionEnforcedByCapOnly, null);
        Assert.assertTrue(actual);
    }


    private boolean testShieldRoleAuthorize(String implementedAction, String returnRole) {
        when(informationService.getRole(UserID, EntityID)).thenReturn(returnRole);
        when(informationService.getEntityType(EntityID)).thenReturn(SAMPLE);

        return shield.authorize(UserID, EntityID, null, implementedAction, null);
    }

    private boolean testShieldEntityAuthorize(String implementedAction, String returnRole, int temperature) {
        MultivaluedMap<String, String> mockedMap = mock(MultivaluedMap.class);
        when(mockedMap.getFirst("ID")).thenReturn(EntityID);

        when(informationService.getRole(UserID, EntityID)).thenReturn(returnRole);
        when(informationService.getEntityType(EntityID)).thenReturn(Entities.EntityType.SAMPLE);

        Entities.Sample sample = new Entities.Sample();
        sample.temperature = temperature;

        when(informationService.getSample(EntityID)).thenReturn(sample);

        return shield.authorize(UserID, EntityID, null, implementedAction, mockedMap);
    }

//    private boolean debugLamdaExp(MultivaluedMap<String, String> map){
//        String key = map.getFirst("ID");
//        Entities.Sample sample = informationService.getSample(key);
//        return (informationService.getSample(map.getFirst("ID")).temperature < 0);
//    }

    private HashSet<String> rolePolicyFreeActions = new HashSet<String>() {{
        add(KeyInRolePolicyFreeActions);
        add(ActionEnforcedByCapOnly);
    }};

    private HashMap<String, HashMap<String, PolicyHandler.Condition>> rolePolicyMap = new HashMap<String, HashMap<String, PolicyHandler.Condition>>() {{
        put(RoleWithPolicy, new HashMap<String, PolicyHandler.Condition>() {{
            put(ImplementedAction, map -> true);
            put(ActionWithEntityPolicy, map -> true);
            put(ImplementedActionWithBoolExp, map -> 1 <= 2);
            put(ImplementedActionWithFalseBoolExp, map -> 1 >= 2);
        }});
    }};

    private HashMap<Entities.EntityType, HashSet> entityPolicyRequiringActions = new HashMap<Entities.EntityType, HashSet>(){{
        put(SAMPLE, new HashSet<String>(){{
            add(ActionWithEntityPolicy);
        }});
        put(ENTITY_TYPES_WITH_NOTHING_FOR_TESTING, new HashSet<String>(){{}});
    }};

    private HashMap<Entities.EntityType, HashMap<String, PolicyHandler.Condition>> entityPolicyMap = new HashMap<Entities.EntityType, HashMap<String, PolicyHandler.Condition>>() {{
        put(SAMPLE, new HashMap<String, PolicyHandler.Condition>(){{
            put(ActionWithEntityPolicy, (map) -> (informationService.getSample(map.getFirst("ID")).temperature < 0));
        }});
    }};

    private static HashSet<String> capabilityRequiringActions = new HashSet<String>() {{
        add("ActionEnforcedByCapOnly");
    }};
}
