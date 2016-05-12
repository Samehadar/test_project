package actors.reply;

import info.smart_tools.smartactors.core.Field;
import info.smart_tools.smartactors.core.FieldName;
import info.smart_tools.smartactors.core.IObject;
import info.smart_tools.smartactors.core.actors.Actor;
import info.smart_tools.smartactors.core.actors.annotations.Handler;
import messages.AfterCheckInDBMessage;
import messages.RegistrationInDBMessage;

import java.util.List;
import java.util.Map;

/**
 * Created by Vitaly on 23.04.2016.
 */
public class RegistrationReplyActor extends Actor {

    public static final Field<String> replyField = new Field<>(new FieldName("Reply"));

    public RegistrationReplyActor(IObject iObject){
        System.out.println(this.getClass().getName());
    }

    @Handler("Handle")
    public void handle(AfterCheckInDBMessage message){

        String reply = "Пользователь " + message.getUsername() + " зарегистрирован.";

        List<Map<String, String>> result = message.getSearchResult();


        respondOn(message, response->{
            replyField.inject(response, reply);
        });
    }

}
