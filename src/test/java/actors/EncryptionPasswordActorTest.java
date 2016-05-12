package actors;

import info.smart_tools.smartactors.core.ChangeValueException;
import info.smart_tools.smartactors.core.ReadValueException;
import info.smart_tools.smartactors.utils.ioc.IOC;
import messages.RegistrationInDBMessage;
import org.junit.Before;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

/**
 * Created by Vitaly on 26.04.2016.
 */
public class EncryptionPasswordActorTest {

    //TODO: check nullable password, and correctly symbol in password

    EncryptionPasswordActor actor;
    String correctlyPassword = "123456";
    String notCorrectlyPassword1 = null;
    String notCorrectlyPassword2 = "1112";
    String hash = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92";

    @Before
    public void setUp() throws Exception {
        actor = new EncryptionPasswordActor(null);
    }

    @Test
    public void correctlyHashPassword() throws Exception {
        RegistrationInDBMessage message = IOC.resolve(RegistrationInDBMessage.class);
        message.setPassword(correctlyPassword);
        actor.handle(message);
        assertEquals(hash, message.getPassword());
    }

    @Test
    public void notNullPassword() throws Exception {
        try {
            RegistrationInDBMessage message = IOC.resolve(RegistrationInDBMessage.class);
            message.setPassword(notCorrectlyPassword1);
            actor.handle(message);
        } catch (Exception e) {
            assertEquals(new IllegalArgumentException("Incorrect password").getMessage(), e.getMessage());
        }
    }

    @Test
    public void correctlyLengthPassword() {
        try {
            RegistrationInDBMessage message = IOC.resolve(RegistrationInDBMessage.class);
            message.setPassword(notCorrectlyPassword2);
            actor.handle(message);
        } catch (Exception e) {
            assertEquals(new IllegalArgumentException("Short password").getMessage(), e.getMessage());
        }

    }
}
