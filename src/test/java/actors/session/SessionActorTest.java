package actors.session;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class SessionActorTest {

    SessionActor actor;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void injectFieldInMessageForSearch() throws Exception{
        SessionMessage message = mock(SessionMessage.class);
        actor.beforeSearchNamesInDB(message);

        verify(message).setPageNumber(1);
        verify(message).setPageSize(100);
        verify(message).setCollectionName("dbnames");
    }
}