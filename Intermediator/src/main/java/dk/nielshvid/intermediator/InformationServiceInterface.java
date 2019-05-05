package dk.nielshvid.intermediator;

import java.util.UUID;

public interface InformationServiceInterface {

    String getRole(String UserID, String EntityID);
    String getEntityType(String EntityID);

    Entities.Person getPerson(String id);
    Entities.Sample getSample(String id);
    Entities.Pizza getPizza(String id);
}