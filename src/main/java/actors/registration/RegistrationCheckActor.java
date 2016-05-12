package actors.registration;

import info.smart_tools.smartactors.core.IMessage;
import info.smart_tools.smartactors.core.IObject;
import info.smart_tools.smartactors.core.actors.Actor;
import info.smart_tools.smartactors.core.actors.annotations.Handler;
import messages.RegistrationCheckMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vitaly on 23.04.2016.
 */
public class RegistrationCheckActor extends Actor {

    public RegistrationCheckActor(IObject iObject){
        System.out.println(this.getClass().getName());
    }

    @Handler("Handle")
    public void handle(RegistrationCheckMessage message){
        System.out.println("Registration check actor");
        String username = message.getUsername();

        Map<String, Map<String, String>> acc = new HashMap<>();

        //add check login
        Map<String, String> criterion = new HashMap<>();
        criterion.put("$eq", username);
        acc.put("login", criterion);

        message.setCollectionName("accounts");
        message.setPageSize(100);
        message.setPageNumber(1);
        message.setQuery(acc);
    }

}
