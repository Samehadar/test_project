package actors;

import actors.authorization.CheckResultLogPassInBaseActor;
import info.smart_tools.smartactors.utils.ioc.IOC;
import messages.AfterCheckInDBMessage;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Vitaly on 26.04.2016.
 */
public class CheckResultLogPassInBaseActorTest {

    CheckResultLogPassInBaseActor actor;
    List<Map<String, String>> list;

    @Before
    public void setUp() throws Exception {
        actor = new CheckResultLogPassInBaseActor(null);
        list = new ArrayList<>();
        Map<String, String> mapa = new HashMap<>();
        mapa.put("andrey", "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92");
        list.add(mapa);
    }

    @Test
    public void correctlyNotFoundUsernameAndPassword() {
        try {
            AfterCheckInDBMessage message = IOC.resolve(AfterCheckInDBMessage.class);
            message.setUsername(null);
            message.setPassword(null);
            actor.handle(message);
        } catch (IllegalArgumentException e){
            assertEquals(new IllegalArgumentException("Incorrect username or password").getMessage(), e.getMessage());
        }

    }
}