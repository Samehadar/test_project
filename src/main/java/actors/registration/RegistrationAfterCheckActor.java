package actors.registration;

import info.smart_tools.smartactors.core.Field;
import info.smart_tools.smartactors.core.FieldName;
import info.smart_tools.smartactors.core.IObject;
import info.smart_tools.smartactors.core.actors.Actor;
import info.smart_tools.smartactors.core.actors.annotations.Handler;
import messages.AfterCheckInDBMessage;

import java.util.List;
import java.util.Map;

/**
 * Created by Vitaly on 24.04.2016.
 */
public class RegistrationAfterCheckActor extends Actor {

    public RegistrationAfterCheckActor(IObject iObject){
        System.out.println(this.getClass().getName());
    }

    @Handler("Handle")
    public void handle(AfterCheckInDBMessage message){

        List<Map<String, String>> results = message.getSearchResult();

        if (results.size() != 0)
            throw new IllegalArgumentException("This username is already busy.");
    }

}
