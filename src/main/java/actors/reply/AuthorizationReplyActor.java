package actors.reply;

import info.smart_tools.smartactors.core.Field;
import info.smart_tools.smartactors.core.FieldName;
import info.smart_tools.smartactors.core.IMessage;
import info.smart_tools.smartactors.core.IObject;
import info.smart_tools.smartactors.core.actors.Actor;
import info.smart_tools.smartactors.core.actors.annotations.Handler;
import messages.AuthorizationMessage;

/**
 * Created by Vitaly on 23.04.2016.
 */
public class AuthorizationReplyActor extends Actor {

    public static final Field<String> replyField = new Field<>(new FieldName("Reply"));

    public AuthorizationReplyActor(IObject iObject){
        System.out.println(this.getClass().getName());
    }

    @Handler("Handle")
    public void handle(AuthorizationMessage message){
        final String reply = "Добро пожаловать " + message.getUsername();

        respondOn(message, response->{
            replyField.inject(response, reply);
        });
    }

}
