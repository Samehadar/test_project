package messages;

import info.smart_tools.smartactors.core.IObject;
import info.smart_tools.smartactors.core.IObjectWrapper;

import java.util.List;
import java.util.Map;

/**
 * Created by Vitaly on 18.04.2016.
 */
public interface SessionMessage extends IObjectWrapper {

    String getSessionId();
    IObject getSession();

    void setSessionId(String sessionId);
    void setSession(IObject session);

}
