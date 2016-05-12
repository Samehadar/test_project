package actors;

import info.smart_tools.smartactors.core.IMessage;
import info.smart_tools.smartactors.core.IObject;
import info.smart_tools.smartactors.core.actors.Actor;
import info.smart_tools.smartactors.core.actors.annotations.Handler;
import messages.RegistrationInDBMessage;

import java.util.regex.Pattern;

/**
 * Created by Vitaly on 27.04.2016.
 */
public class CheckValidSymbolActor extends Actor {

    // username must be not null and
    // must contain at least one of the symbols from {@literal 0-9a-zA-Z_-+=|!@#$%^&*:/., {}()[]}
    private static final Pattern validSymbols = Pattern.compile("[\\w\\-\\+=\\|!@#\\$%\\^&\\*:/\\., \\{\\}\\(\\)\\[\\]]+", Pattern.UNICODE_CHARACTER_CLASS);

    public CheckValidSymbolActor(IObject iObject){
        System.out.println(this.getClass().getName());
    }

    @Handler("Handle")
    public void handle(RegistrationInDBMessage message) throws IllegalArgumentException{
        String username = message.getUsername();
        String password = message.getPassword();

        if (username == null || password == null) {
            throw new IllegalArgumentException("Parameters must not be null");
        }
        if (username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Parameters must not be empty");
        }
        if (!validSymbols.matcher(username).matches() || !validSymbols.matcher(password).matches()) {
            throw new IllegalArgumentException("Parameters contains illegal symbols");
        }
    }

}
