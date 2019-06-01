package dk.nielshvid.intermediary;

import javax.ws.rs.core.MultivaluedMap;

public interface InformationServiceInterface {

    String getRoleByEntity(String UserID, String EntityID);
    String getRoleByOrganization(String UserID, String OrganizationID);
    String getEntityType(MultivaluedMap<String, String> QPmap);
    
    Entities.Sample getSample(String id);
    Entities.Blood getBlood(String id);
}
