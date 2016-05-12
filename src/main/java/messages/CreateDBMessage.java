package messages;

import info.smart_tools.smartactors.core.IObjectWrapper;

import java.util.Map;

/**
 * Created by Vitaly on 16.04.2016.
 */
public interface CreateDBMessage extends IObjectWrapper {

    Map<String, String> getIndexes();
    String getCollectionName();

    void setIndexes(Map<String, String> indexes);
    void setCollectionName(String collectionName);

}
