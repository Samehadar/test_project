package messages;

import info.smart_tools.smartactors.core.IObjectWrapper;

import java.util.List;
import java.util.Map;

/**
 * Created by Vitaly on 20.04.2016.
 */
public interface AfterCheckInDBMessage extends IObjectWrapper {

    String getUsername();
    String getPassword();
    List<Map<String, String>> getSearchResult();
    String getMessageMapId();

    void setUsername(String username);
    void setPassword(String password);
    void setSearchResult(List<Map<String, String>> searchResult);
    void setMessageMapId(String messageMapId);

}
