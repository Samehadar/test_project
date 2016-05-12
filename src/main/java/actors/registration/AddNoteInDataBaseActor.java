package actors.registration;

import info.smart_tools.smartactors.core.IObject;
import info.smart_tools.smartactors.core.actors.Actor;
import info.smart_tools.smartactors.core.actors.annotations.Handler;
import messages.RegistrationInDBMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vitaly on 17.04.2016.
 */
public class AddNoteInDataBaseActor extends Actor {

    public AddNoteInDataBaseActor(IObject iObject){
        System.out.println(this.getClass().getName());
    }

    @Handler("Handle")
    public void handle(RegistrationInDBMessage message){
        message.setCollectionName("accounts");
        List<Map<String, String>> document = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("login", message.getUsername());
        map.put("password", message.getPassword());
        document.add(map);

        message.setDocuments(document);
    }

}
