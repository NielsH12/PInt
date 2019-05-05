package dk.nielshvid.intermediator.Test;

import dk.nielshvid.intermediator.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


// Frameworks:
// TestNG: https://testng.org/doc/
// Mockito: https://site.mockito.org/

public class TestGuard {
    private Guard guard;
    private String keyInRolePolicyFreeActions = "KeyInRolePolicyFreeActions";
    private String keyNotInRolePolicyFreeActions = "keyNotInRolePolicyFreeActions";
    private String RoleWithPolicy = "RoleWithPolicy";
    private String ImplementedAction = "ImplementedAction";
    private String ImplementedActionWithBoolExp = "ImplementedActionWithBoolExp";
    private String ImplementedActionWithFalseBoolExp = "ImplementedActionWithFalseBoolExp";
    private String ActionWithEntityPolicy = "ActionWithEntityPolicy";
    private String EntityWithNoPolicies = "EntityWithNoPolicies";
    private String ActionEnforcedByCapOnly = "ActionEnforcedByCapOnly";
    private InformationServiceInterface informationService;
    private UUID expectedUUID = UUID.randomUUID();
    private UUID ValidCapaID = UUID.randomUUID();

    @BeforeMethod
    public void setUp() {
        String UserID = "userID";
        String EntityID = "entityID";

        informationService = mock(InformationServiceInterface.class);
        when(informationService.getRole(UserID, EntityID)).thenReturn(RoleWithPolicy);
        when(informationService.getEntityType(EntityWithNoPolicies)).thenReturn(EntityWithNoPolicies);

        PolicyHandler PH = new PolicyHandler(rolePolicyMap, entityPolicyMap, informationService);

        CapabilityHandler CH = mock(CapabilityHandler.class);
        when(CH.addCapability(anyString(), anyString(), anyString())).thenReturn(expectedUUID);
        when(CH.authorize(UserID, EntityWithNoPolicies, ValidCapaID, ActionEnforcedByCapOnly)).thenReturn(true);

        guard = new Guard(informationService, rolePolicyFreeActions, capabilityRequiringActions, entityPolicyRequiringActions, PH, CH);
    }

    //rolePolicyFreeActions
    @Test
    public void Action_in_rolePolicyFreeActions_ReturnsTrue() {
        final boolean actual = guard.authorize(null, EntityWithNoPolicies, null, keyInRolePolicyFreeActions, null);
        //
        Assert.assertTrue(actual);
    }

    @Test(expectedExceptions = {WebApplicationException.class}, expectedExceptionsMessageRegExp = "RP: Permission denied")
    public void Action_notIn_rolePolicyFreeActions_ReturnsException() {

        guard.authorize(null, null, null, keyNotInRolePolicyFreeActions, null);
    }

    //rolePolicies
    @Test
    public void User_with_the_Right_role_AreAuthorized() {
        Assert.assertTrue(testGuardRoleAuthorize(ImplementedAction, RoleWithPolicy));
    }

    @Test(expectedExceptions = {WebApplicationException.class}, expectedExceptionsMessageRegExp = "RP: Permission denied")
    public void User_with_the_Wrong_role_ReturnsException() {
        testGuardRoleAuthorize(ImplementedAction, "RoleWithNoPolicy");
    }

    @Test
    public void User_with_the_Right_role_And_trueBooleanExp_AreAuthorized() {
        Assert.assertTrue(testGuardRoleAuthorize(ImplementedActionWithBoolExp, RoleWithPolicy));
    }


    @Test(expectedExceptions = {WebApplicationException.class}, expectedExceptionsMessageRegExp = "RP: Permission denied")
    public void User_with_the_Right_role_And_falseBooleanExp_ReturnsException() {
        Assert.assertTrue(testGuardRoleAuthorize(ImplementedActionWithFalseBoolExp, RoleWithPolicy));
    }

    //entityPolicy
    @Test
    public void User_with_the_Right_role_And_trueEntityExp_AreAuthorized() {
        Assert.assertTrue(testGuardEntityAuthorize(ActionWithEntityPolicy, RoleWithPolicy, -1));
    }

    @Test(expectedExceptions = {WebApplicationException.class}, expectedExceptionsMessageRegExp = "EP: Permission denied")
    public void User_with_the_Right_role_And_falseEntityExp_AreNoRAuthorized() {
       testGuardEntityAuthorize(ActionWithEntityPolicy, RoleWithPolicy, 1);
    }

    // CapabilityPolicies
    @Test
    public void GenerateCapability_returns_UUID_WhenNoPolicyIsBlocking() {
        UUID actual = guard.generateCapability("userID", "entityID", keyInRolePolicyFreeActions, null);
        Assert.assertEquals(actual, expectedUUID);
    }

    @Test
    public void GenerateCapability_Are_Enforced_By_Policies() {
        // fail rolePolicy
        UUID expectedNull = guard.generateCapability("userID", "entityID", keyNotInRolePolicyFreeActions, null);
        Assert.assertNull(expectedNull);

        // pass rolePolicy
        UUID actual = guard.generateCapability("userID", "entityID", ImplementedActionWithBoolExp, null);
        Assert.assertEquals(actual, expectedUUID);
    }

    @Test(expectedExceptions = {WebApplicationException.class}, expectedExceptionsMessageRegExp = "CP: Invalid capability")
    public void capabilityPoliciesAreEnforced_WithWrongCapabilityID_ThrowException() {
        guard.authorize("userID", EntityWithNoPolicies, null, ActionEnforcedByCapOnly, null);
    }

    @Test
    public void capabilityPoliciesAreEnforced_WithRightCapabilityID_ReturnsTrue() {
        final boolean actual = guard.authorize("userID", EntityWithNoPolicies, ValidCapaID, ActionEnforcedByCapOnly, null);
        Assert.assertTrue(actual);
    }


    private boolean testGuardRoleAuthorize(String implementedAction, String returnRole) {
        String UserID = "userID";
        String EntityID = "entityID";

        when(informationService.getRole(UserID, EntityID)).thenReturn(returnRole);
        when(informationService.getEntityType(EntityID)).thenReturn("entityType");

        return guard.authorize(UserID, EntityID, null, implementedAction, null);
    }

    private boolean testGuardEntityAuthorize(String implementedAction, String returnRole, int temperature) {
        String UserID = "userID";
        String EntityID = "entityID";
        MultivaluedMap<String, String> mockedMap = mock(MultivaluedMap.class);
        when(mockedMap.getFirst("ID")).thenReturn(EntityID);

        when(informationService.getRole(UserID, EntityID)).thenReturn(returnRole);
        when(informationService.getEntityType(EntityID)).thenReturn("entityType");

        Entities.Sample sample = new Entities.Sample();
        sample.temperature = temperature;

        when(informationService.getSample(EntityID)).thenReturn(sample);

        return guard.authorize(UserID, EntityID, null, implementedAction, mockedMap);
    }

//    private boolean debugLamdaExp(MultivaluedMap<String, String> map){
//        String key = map.getFirst("ID");
//        Entities.Sample sample = informationService.getSample(key);
//        return (informationService.getSample(map.getFirst("ID")).temperature < 0);
//    }

    private HashSet<String> rolePolicyFreeActions = new HashSet<String>() {{
        add(keyInRolePolicyFreeActions);
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

    private HashMap<String, HashSet> entityPolicyRequiringActions = new HashMap<String, HashSet>(){{
        put("entityType", new HashSet<String>(){{
            add(ActionWithEntityPolicy);
        }});
        put("EntityWithNoPolicies", new HashSet<String>(){{}});
    }};

    private HashMap<String, HashMap<String, PolicyHandler.Condition>> entityPolicyMap = new HashMap<String, HashMap<String, PolicyHandler.Condition>>() {{
        put("entityType", new HashMap<String, PolicyHandler.Condition>(){{
            put(ActionWithEntityPolicy, (map) -> (informationService.getSample(map.getFirst("ID")).temperature < 0));
        }});
    }};

    private static HashSet<String> capabilityRequiringActions = new HashSet<String>() {{
        add("ActionEnforcedByCapOnly");
    }};
}
