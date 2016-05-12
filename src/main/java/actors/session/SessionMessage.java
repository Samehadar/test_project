package actors.session;

import info.smart_tools.smartactors.core.IObject;
import info.smart_tools.smartactors.core.IObjectWrapper;

import java.util.Collection;
import java.util.List;

interface SessionMessage extends IObjectWrapper {
    String getSessionId();
    void setSessionId(String sessionId);
    void setSession(IObject session);
    IObject getSession();

    void setQuery(IObject query);
    void setDocuments(Collection<IObject> documents);

    void setCollectionName(String collectionName);
    void setPageSize(Integer pageSize);

    void setPageNumber(Integer pageNumber);
    List<IObject> getSearchResult();
}
