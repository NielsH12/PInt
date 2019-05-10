package dk.nielshvid.intermediary;

public interface InformationServiceInterface {

    String getRole(String UserID, String EntityID);
    Entities.EntityTypes getEntityType(String EntityID);

    Entities.Person getPerson(String id);
    Entities.Sample getSample(String id);
    Entities.Pizza getPizza(String id);
}