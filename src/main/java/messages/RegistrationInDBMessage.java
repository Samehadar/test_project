package messages;

import info.smart_tools.smartactors.core.IObjectWrapper;

import java.util.List;
import java.util.Map;

/**
 * Created by Vitaly on 13.04.2016.
 */
public interface RegistrationInDBMessage extends IObjectWrapper {

    String getUsername();
    String getPassword();
    String getCollectionName();
    List<Map<String, String>> getDocuments();

    void setUsername(String username);
    void setPassword(String password);
    void setCollectionName(String collectionName);
    void setDocuments(List<Map<String, String>> documents);

}
