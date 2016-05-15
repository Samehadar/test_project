package actors;

import com.google.common.util.concurrent.Uninterruptibles;
import info.smart_tools.smartactors.core.*;
import info.smart_tools.smartactors.core.actors.Actor;
import info.smart_tools.smartactors.core.actors.ActorSystem;
import info.smart_tools.smartactors.core.actors.ActorSystemBuilder;
import info.smart_tools.smartactors.core.actors.connection.ActorCreator;
import info.smart_tools.smartactors.core.addressing.targeting.actors.ActorPath;
import info.smart_tools.smartactors.core.impl.SMObjectBuilder;
import info.smart_tools.smartactors.testing.actors.ActionBasedActor;
import info.smart_tools.smartactors.testing.actors.MessageHandler;
import info.smart_tools.smartactors.utils.reflection.URLHelper;
import org.junit.After;
import org.junit.Before;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ActorsIntegrationTestBase {
    protected static final int MESSAGE_RECEIVING_TIMEOUT_MS = 4000;
    protected ActorSystem system;
    protected MessageHandler handlerMock;

    @Before
    public void setUp() throws java.util.concurrent.ExecutionException, InterruptedException {
        ActorCreator.registrar.loadActorsFrom(Arrays.asList(URLHelper.getCurrentClassPath()));
        system = new ActorSystemBuilder()
                .withThreads(2)
                .build();
        system.start().get();
        handlerMock = mock(MessageHandler.class);
    }

    @After
    public void tearDown() throws java.util.concurrent.ExecutionException, InterruptedException {
        system.stop().get();
    }

    protected List<IMessage> verifyMessageWasReceivedBy(MessageHandler handler) {
        return verifyMessageWasReceivedBy(handler, 1);
    }

    protected List<IMessage> verifyMessageWasReceivedBy(MessageHandler handler, int numberOfTimes) {
        ArgumentCaptor<IMessage> messagesCaptor = ArgumentCaptor.forClass(IMessage.class);
        verify(handler, timeout(MESSAGE_RECEIVING_TIMEOUT_MS).times(numberOfTimes)).accept(any(Actor.class), messagesCaptor.capture());
        return messagesCaptor.getAllValues();
    }

    protected void verifyHandlerWasNotInvoked(MessageHandler handler) {
        Uninterruptibles.sleepUninterruptibly(20, TimeUnit.MILLISECONDS);
        verifyZeroInteractions(handler);
    }

    protected void connectTestActor(ActorPath pathToTestActor, MessageHandler handler) {
        ActionBasedActor actor = new ActionBasedActor(handler);
        system.connect(pathToTestActor, actor);
    }

    /*
    protected MessageHandler responding(MessageModification modification) {
        return (actor, message) -> actor.respondOn(message, modification);
    }

    protected MessageHandler doingNothingOnRequest() {
        return (actor, message) -> { };
    }

    protected MessageHandler respondingWithType(String type) {
        return responding((response) -> {
            response.setValue(new FieldName("type"), type);
        });
    }*/

    protected IMessage typedMessageTo(ActorPath path, String type) {
        IMessage message = null;
        try {
            IObject msgContent = new SMObjectBuilder()
                    .with("address", new SMObjectBuilder()
                                    .with("target", path)
                                    .with("handler", ActorPath.fromString(type))
                                    .build()
                    )
                    .build();
            message = new Message(msgContent);
        } catch (ChangeValueException e) {
            // ignoring it
        }

        return message;
    }
}
