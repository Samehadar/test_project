package actors.authorization;

import info.smart_tools.smartactors.core.IObject;
import info.smart_tools.smartactors.core.actors.Actor;
import info.smart_tools.smartactors.core.actors.annotations.Handler;
import messages.SessionMessage;

/**
 * Created by Vitaly on 30.04.2016.
 */
public class WorkWithCookieActor extends Actor {

    public WorkWithCookieActor(IObject iObject){
        System.out.println(this.getClass().getName());
    }

    @Handler("Handle")
    public void handle(SessionMessage message){
        System.out.println(message.getSessionId());
    }

}
