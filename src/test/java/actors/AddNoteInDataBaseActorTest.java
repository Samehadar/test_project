package actors;

import actors.registration.AddNoteInDataBaseActor;
import info.smart_tools.smartactors.utils.ioc.IOC;
import messages.RegistrationInDBMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Vitaly on 27.04.2016.
 */
public class AddNoteInDataBaseActorTest {

    AddNoteInDataBaseActor actor;
    String username = "andrey";
    String password = "1112";
    String collectionName = "accounts";
    List<Map<String, String>> document;

    @Before
    public void setUp() throws Exception {
        actor = new AddNoteInDataBaseActor(null);
        document = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("login", username);
        map.put("password", password);
        document.add(map);
    }

    @Test
    public void correctlyAddingInDB() throws Exception {
        RegistrationInDBMessage message = IOC.resolve(RegistrationInDBMessage.class);
        message.setUsername(username);
        message.setPassword(password);
        actor.handle(message);
        assertEquals(message.getDocuments(), document);
    }

    @Test
    public void correctlyDBName() throws Exception {
        RegistrationInDBMessage message = IOC.resolve(RegistrationInDBMessage.class);
        message.setUsername(username);
        message.setPassword(password);
        actor.handle(message);
        assertEquals(message.getCollectionName(), collectionName);
    }

    @Test
    public void verifyMessage() throws Exception{
        RegistrationInDBMessage message = mock(RegistrationInDBMessage.class);
        actor.handle(message);
        verify(message).getPassword();
        verify(message).getUsername();
        verify(message).setCollectionName("accounts");
    }
}