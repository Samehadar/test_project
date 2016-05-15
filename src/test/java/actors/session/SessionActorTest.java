package actors.session;

import info.smart_tools.smartactors.core.*;
import info.smart_tools.smartactors.core.addressing.AddressingFields;
import info.smart_tools.smartactors.core.addressing.maps.MessageMap;
import info.smart_tools.smartactors.core.addressing.targeting.actors.ActorPath;
import info.smart_tools.smartactors.core.impl.SMObjectBuilder;
import info.smart_tools.smartactors.utils.ioc.IOC;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.exceptions.ExceptionIncludingMockitoWarnings;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class SessionActorTest {

    SessionActor actor;

    @Before
    public void setUp() throws Exception {
        actor = new SessionActor(new SMObjectBuilder()
                        .with("databaseActorPath", "dbActor")
                        .with("sessionActorPath", "sessionActor")
                        .with("collectionName", "dbnames")
                        .build());
    }

    @Test
    public void injectFieldInMessageForSearch() throws Exception{
        SessionMessage message = mock(SessionMessage.class);
        actor.beforeSearchNamesInDB(message);

        verify(message).setPageNumber(1);
        verify(message).setPageSize(100);
        verify(message).setCollectionName("dbnames");
    }

    private IObject createInitIObject() throws ReadValueException {
        IObject conf = mock(IObject.class);
        when(conf.getValue(eq(new FieldName("name")))).thenReturn("sessionActor");
        when(conf.getValue(eq(new FieldName("databaseActorPath")))).thenReturn("dbActor");
        when(conf.getValue(eq(new FieldName("collectionName")))).thenReturn("dbnames");
        return conf;
    }

    //search when  initialize db's
    @Test(expected = ReadValueException.class)
    public void resultFromSearchInDBIsNull() throws ReadValueException {
        SessionMessage message = mock(SessionMessage.class);
        when(message.getSearchResult()).thenReturn(null);

        actor.loadNames(message);
        verify(message.getSearchResult(),times(1));
    }

    @Test(expected = ReadValueException.class)
    public void gettingSession_When_DBnamesIsNotInitialize() throws ReadValueException, ChangeValueException {
        SessionMessage message = mock(SessionMessage.class);
        when(message.getSessionId()).thenReturn(null);

        actor.getSessionHandler(message);
        verify(message.getSessionId());

        assertEquals(notNull(), message.getSessionId());
        assertEquals(null, message.getSession());
    }

}