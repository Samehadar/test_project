package actors.reply;

import info.smart_tools.smartactors.core.*;
import info.smart_tools.smartactors.core.actors.Actor;
import info.smart_tools.smartactors.core.actors.annotations.Handler;
import messages.RegistrationInDBMessage;

/**
 * Created by Vitaly on 16.04.2016.
 */
public class ReplyActor extends Actor {

    public static final Field<String> replyField = new Field<>(new FieldName("Reply"));


    public ReplyActor(IObject iObject){
        System.out.println(this.getClass().getName());
    }

    @Handler("Handle")
    public void handle(RegistrationInDBMessage message) throws ReadValueException, ChangeValueException {

        String reply = "Скорее всего коллекция создана.";

        respondOn(message, response->{
            replyField.inject(response, reply);
        });
    }

}
