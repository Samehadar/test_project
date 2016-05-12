package actors.session;

import org.junit.Before;

import static org.junit.Assert.*;

public class SessionActorTest {

    SessionActor actor;

    @Before
    public void setUp() throws Exception {
        actor = new SessionActor(null);
    }
}