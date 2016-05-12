package messages;

import info.smart_tools.smartactors.core.IObjectWrapper;

import java.util.Map;

/**
 * Created by Vitaly on 23.04.2016.
 */
public interface RegistrationCheckMessage extends IObjectWrapper {

    String getUsername();
    String getPassword();
    String getCollectionName();
    Integer getPageSize();
    Integer getPageNumber();
    Map<String, Map<String, String>> getQuery();

    void setUsername(String username);
    void setPassword(String password);
    void setCollectionName(String collectionName);
    void setPageSize(Integer pageSize);
    void setPageNumber(Integer pageNumber);
    void setQuery(Map<String, Map<String, String>> query);

}
