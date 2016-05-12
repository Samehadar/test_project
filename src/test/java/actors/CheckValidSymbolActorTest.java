package actors;

import info.smart_tools.smartactors.utils.ioc.IOC;
import messages.RegistrationInDBMessage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Vitaly on 27.04.2016.
 */
public class CheckValidSymbolActorTest {

    CheckValidSymbolActor actor;

    @Before
    public void setUp() throws Exception {
        actor = new CheckValidSymbolActor(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void notNullName() throws Exception {
        RegistrationInDBMessage message = IOC.resolve(RegistrationInDBMessage.class);
        message.setUsername(null);
        actor.handle(message);
    }

    @Test(expected = IllegalArgumentException.class)
    public void notEmptyName() throws Exception{
        RegistrationInDBMessage message = IOC.resolve(RegistrationInDBMessage.class);
        message.setUsername("");
        actor.handle(message);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validSymbol() throws Exception{
        RegistrationInDBMessage message = IOC.resolve(RegistrationInDBMessage.class);
        message.setUsername("\\\\");
        actor.handle(message);
    }
}