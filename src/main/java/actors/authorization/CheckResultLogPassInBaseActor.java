package actors.authorization;

import info.smart_tools.smartactors.core.IObject;
import info.smart_tools.smartactors.core.actors.Actor;
import info.smart_tools.smartactors.core.actors.annotations.Handler;
import messages.AfterCheckInDBMessage;

import java.util.List;
import java.util.Map;

/**
 * Created by Vitaly on 20.04.2016.
 */
public class CheckResultLogPassInBaseActor extends Actor {

    public CheckResultLogPassInBaseActor(IObject iObject){
        System.out.println(this.getClass().getName());
    }

    //TODO: if authoriz accept - giving cookies, else send error message
    //TODO: возможно стоит сделать хранение sessionId у пользователя
    @Handler("Handle")
    public void handle(AfterCheckInDBMessage message) throws IllegalArgumentException{
        List<Map<String, String>> maps = message.getSearchResult();
        if (maps == null || maps.size() == 0)
            throw new IllegalArgumentException("Incorrect username or password");

        System.out.println(maps);
    }

}
