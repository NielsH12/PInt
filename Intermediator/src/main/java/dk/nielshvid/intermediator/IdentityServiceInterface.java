package dk.nielshvid.intermediator;

import java.util.UUID;

public interface IdentityServiceInterface {

    String getRole(UUID UserID, String BoxID);
}
