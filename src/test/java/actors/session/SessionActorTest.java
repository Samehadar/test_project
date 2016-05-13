package actors.session;

import info.smart_tools.smartactors.core.FieldName;
import info.smart_tools.smartactors.core.IObject;
import info.smart_tools.smartactors.core.ReadValueException;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class SessionActorTest {

    SessionActor actor;

    @Before
    public void setUp() throws Exception {
        actor = new SessionActor(createInitIObject());

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
}