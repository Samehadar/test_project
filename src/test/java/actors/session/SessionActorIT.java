package actors.session;

import actors.ActorsIntegrationTestBase;
import info.smart_tools.smartactors.core.*;
import info.smart_tools.smartactors.core.addressing.AddressingFields;
import info.smart_tools.smartactors.core.addressing.maps.MessageMap;
import info.smart_tools.smartactors.core.addressing.maps.StraightMessageMap;
import info.smart_tools.smartactors.core.addressing.targeting.Target;
import info.smart_tools.smartactors.core.addressing.targeting.Targets;
import info.smart_tools.smartactors.core.addressing.targeting.actors.ActorPath;
import info.smart_tools.smartactors.core.impl.SMObject;
import info.smart_tools.smartactors.core.impl.SMObjectBuilder;
import info.smart_tools.smartactors.core.routers.MessageBus;
import info.smart_tools.smartactors.utils.ioc.IOC;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class SessionActorIT extends ActorsIntegrationTestBase{

    SessionActor actor;
    private IObject search;
    private IObject load;
    private IObject getSessionTarget;
    private IObject setSessionFieldTarget;
    private IObject handleSessionFromDBTarget;
    private IObject saveSessionFieldTarget;
    private IObject testReceiverTarget;
    private IObject upsertSessionTarget;

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        super.setUp();

        try {
            actor = new SessionActor(new SMObjectBuilder()
                    .with("sessionActorPath", "sessionActor")
                    .with("databaseActorPath", "dbActor")
                    .with("collectionName", "dbnames")
                    .build());
            ActorPath sessionsActorPath = ActorPath.fromString("sessionActor");
            ActorPath mockActorPath = ActorPath.fromString("mock");
            system.connect(sessionsActorPath, actor);
            connectTestActor(mockActorPath, handlerMock::accept);

            search = new SMObjectBuilder()
                    .with("target", sessionsActorPath)
                    .with("handler", ActorPath.fromString("search"))
                    .build();
            load = new SMObjectBuilder()
                    .with("target", sessionsActorPath)
                    .with("handler", ActorPath.fromString("load"))
                    .build();
            getSessionTarget = new SMObjectBuilder()
                    .with("target", sessionsActorPath)
                    .with("handler", ActorPath.fromString("getSession"))
                    .build();
            setSessionFieldTarget = new SMObjectBuilder()
                    .with("target", sessionsActorPath)
                    .with("handler", ActorPath.fromString("setSessionField"))
                    .build();
            handleSessionFromDBTarget = new SMObjectBuilder()
                    .with("target", sessionsActorPath)
                    .with("handler", ActorPath.fromString("handleSessionFromDB"))
                    .build();
            saveSessionFieldTarget = new SMObjectBuilder()
                    .with("target", sessionsActorPath)
                    .with("handler", ActorPath.fromString("saveSession"))
                    .build();
            upsertSessionTarget = new SMObjectBuilder()
                    .with("target", sessionsActorPath)
                    .with("handler", ActorPath.fromString("upsertSession"))
                    .build();

            testReceiverTarget = new SMObjectBuilder()
                    .with("target", mockActorPath)
                    .with("handler", ActorPath.fromString("handler"))
                    .build();

        } catch (Exception e) {}
    }


    @Test
    public void Should_createNewSessionAndReturnItAsIObject_When_SessionIdIsNotPresent()
            throws Exception {
        IMessage message = new Message(messageWithMapOf(getSessionTarget, testReceiverTarget).build());

        MessageBus.send(message);

        verifyMessageWasReceivedBy(handlerMock);

        assertNotNull(message.getValue(new FieldName("sessionId")));

        assertNotNull(message.getValue(new FieldName("session")));
        assertTrue(IObject.class.isAssignableFrom(message.getValue(new FieldName("session")).getClass()));
    }

    @Test
    public void gettingSession_When_SessionIdIsNull() throws ReadValueException, ChangeValueException {
        Field<Integer> idPartField = new Field<>(new FieldName("id"));
        Field<String> fullNameField = new Field<>(new FieldName("name"));

        List<IObject> dbnames = new ArrayList<>();
        IObject sessions = IOC.resolve(IObject.class);
        idPartField.inject(sessions, 1);
        fullNameField.inject(sessions, "sessions1");
        dbnames.add(sessions);
        idPartField.inject(sessions, 2);
        fullNameField.inject(sessions, "sessions2");
        dbnames.add(sessions);
        idPartField.inject(sessions, 3);
        fullNameField.inject(sessions, "sessions3");
        dbnames.add(sessions);
        idPartField.inject(sessions, 4);
        fullNameField.inject(sessions, "sessions4");
        dbnames.add(sessions);

        IMessage message0 = new Message(messageWithMapOf(load, getSessionTarget, testReceiverTarget).build());
        message0.setValue(new FieldName("searchResult"), dbnames);
        message0.setValue(new FieldName("sessionId"), null);
        IMessage message = spy(message0);

        MessageBus.send(message);

        verifyMessageWasReceivedBy(handlerMock);

        /*
        when(message.getValue(new FieldName("sessionId"))).thenReturn(null);
        when(message.getValue(new FieldName("searchResult"))).thenReturn(dbnames);
        */

        verify(message).getValue(new FieldName("searchResult"));
        verify(message).getValue(new FieldName("sessionId"));

        //need fix this
//        verify(message).setValue(new FieldName("sessionId"), eq(anyString()));
//        verify(message).setValue(new FieldName("session"), eq(any()));

        assertNotNull(message.getValue(new FieldName("session")));
        assertNotNull(message.getValue(new FieldName("sessionId")));

        assertTrue(IObject.class.isAssignableFrom(message.getValue(new FieldName("session")).getClass()));
    }


    @Test
    public void Should_insertDbActorIntoMM_When_SaveSessionIsCalled()
            throws Exception {
        IMessage message = new Message(messageWithMapOf(saveSessionFieldTarget, testReceiverTarget).build());
        message.setValue(new FieldName("session"), new SMObject());

        MessageBus.send(message);

        verifyHandlerWasNotInvoked(handlerMock);

        MessageMap messageMap = (MessageMap) message.getValue(new FieldName("messageMap"));
        assertNotNull(messageMap);
        List<IObject> targets = messageMap.getTargets();
        assertNotNull(targets);

        assertEquals(targets.size(), 4);
        IObject dbTarget = targets.get(1);

        assertEquals(AddressingFields.HANDLER_FIELD.from(dbTarget, Target.class), ActorPath.fromString("find-documents"));
    }

    @Test
    public void Should_buildInsertQuery_When_SaveSessionIsCalledAndSessionExists()
            throws Exception {
        IMessage message1 = new Message(messageWithMapOf(getSessionTarget, testReceiverTarget).build());

        MessageBus.send(message1);

        verifyMessageWasReceivedBy(handlerMock);

        IMessage message2 = new Message(messageWithMapOf(saveSessionFieldTarget, testReceiverTarget).build());
        Object sessionId = message1.getValue(new FieldName("sessionId"));
        message2.setValue(new FieldName("sessionId"), sessionId);

        MessageBus.send(message2);

        verifyHandlerWasNotInvoked(handlerMock);

        MessageMap messageMap = (MessageMap) message2.getValue(new FieldName("messageMap"));
        assertNotNull(messageMap);
        List<IObject> targets = messageMap.getTargets();
        assertNotNull(targets);

        assertEquals(targets.size(), 3);
        IObject dbTarget = targets.get(1);

        assertEquals(AddressingFields.HANDLER_FIELD.from(dbTarget, Target.class), ActorPath.fromString("insert-documents"));

        List<?> documents = (List<?>)message2.getValue(new FieldName("documents"));

        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(((IObject)documents.get(0)).getValue(new FieldName("sessionId")), sessionId);
    }

    @Test
    public void Should_buildUpdateQuery_When_gotSessionFromDBWhileSavingSession()
            throws Exception {
        IMessage message1 = new Message(messageWithMapOf(getSessionTarget, testReceiverTarget).build());
        MessageBus.send(message1);
        verifyMessageWasReceivedBy(handlerMock);

        IMessage message2 = new Message(messageWithMapOf(upsertSessionTarget, testReceiverTarget).build());
        message2.setValue(new FieldName("sessionId"), message1.getValue(new FieldName("sessionId")));
        message2.setValue(new FieldName("session"), new SMObject());
        message2.setValue(new FieldName("searchResult"), new LinkedList<IObject>(){{
            add(new SMObjectBuilder().with("id", 100500l).build());
        }});
        MessageBus.send(message2);
        verifyHandlerWasNotInvoked(handlerMock);

        MessageMap messageMap = (MessageMap) message2.getValue(new FieldName("messageMap"));
        assertNotNull(messageMap);
        List<IObject> targets = messageMap.getTargets();
        assertNotNull(targets);

        assertEquals(targets.size(), 3);
        IObject dbTarget = targets.get(1);

        assertEquals(AddressingFields.HANDLER_FIELD.from(dbTarget, Target.class), ActorPath.fromString("update-documents"));

        List<?> documents = (List<?>)message2.getValue(new FieldName("documents"));

        assertNotNull(documents);
        assertEquals(documents.size(), 1);
        assertEquals(((IObject)documents.get(0)).getValue(new FieldName("id")), 100500l);
    }

    @Test
    public void Should_insertDbAndSessionActorsIntoMM_When_CacheDoesNotContainSession()
            throws Exception {
        IMessage message = new Message(messageWithMapOf(getSessionTarget, testReceiverTarget).build());
        message.setValue(new FieldName("sessionId"), UUID.randomUUID().toString());

        MessageBus.send(message);

        verifyHandlerWasNotInvoked(handlerMock);

        MessageMap messageMap = (MessageMap) message.getValue(new FieldName("messageMap"));
        assertNotNull(messageMap);
        List<IObject> targets = messageMap.getTargets();
        assertNotNull(targets);

        assertEquals(targets.size(), 4);
        IObject dbTarget = targets.get(1);
        IObject sessionTarget = targets.get(2);

        assertEquals(AddressingFields.HANDLER_FIELD.from(dbTarget, Target.class), ActorPath.fromString("find-documents"));
        assertEquals(AddressingFields.HANDLER_FIELD.from(sessionTarget, Target.class), ActorPath.fromString("handleSessionFromDB"));
    }

    @Test
    public void Should_insertSessionFromSearchResult_When_ResultIsNotEmpty() throws Exception {

        IMessage message = new Message(messageWithMapOf(handleSessionFromDBTarget, testReceiverTarget).build());
        List<IObject> sessions = new ArrayList<>();
        String sessionId = UUID.randomUUID().toString();
        IObject session = new SMObjectBuilder()
                .with("sessionId", sessionId)
                .build();
        sessions.add(session);
        message.setValue(new FieldName("searchResult"), sessions);

        MessageBus.send(message);

        verifyMessageWasReceivedBy(handlerMock);

        IObject sessionAfterHandler = (IObject) message.getValue(new FieldName("session"));
        assertNotNull(sessionAfterHandler);
        assertNotNull(sessionAfterHandler.getValue(new FieldName("sessionId")));


    }

    @Test
    public void Should_insertExchangingWithError_When_ResultIsEmpty() throws Exception {

        IMessage message = new Message(messageWithMapOf(handleSessionFromDBTarget, testReceiverTarget).build());
        message.setValue(new FieldName("searchResult"), null);

        MessageBus.send(message);

        verifyHandlerWasNotInvoked(handlerMock);

        MessageMap messageMap = (MessageMap) message.getValue(new FieldName("messageMap"));
        assertNotNull(messageMap);
        List<IObject> targets = messageMap.getTargets();
        assertNotNull(targets);

        assertEquals(targets.size(), 4);
        IObject exchanging = targets.get(1);
        IObject finish = targets.get(2);

        assertEquals(AddressingFields.TARGET_FIELD.from(exchanging, Target.class), ActorPath.fromString("exchanging"));
        assertEquals(AddressingFields.TARGET_FIELD.from(finish, Target.class), Targets.FINISHED_MAP);
    }

    @Test
    public void Should_writeMessageFieldToSession_WhenMessagePassedToSetSessionFieldHandler()
            throws Exception {
        Object value = new Object();

        setSessionFieldTarget.setValue(new FieldName("parameters"),
                new SMObjectBuilder()
                        .with("from", "sourceField")
                        .with("to", "targetField")
                        .build());

        IMessage firstMessage = new Message(messageWithMapOf(getSessionTarget, testReceiverTarget).build());

        MessageBus.send(firstMessage);

        verifyMessageWasReceivedBy(handlerMock);

        IMessage secondMessage = new Message(messageWithMapOf(setSessionFieldTarget, testReceiverTarget)
                .with("sourceField", value)
                .with("sessionId", firstMessage.getValue(new FieldName("sessionId")))
                .build());

        MessageBus.send(secondMessage);

        verifyMessageWasReceivedBy(handlerMock, 2);

        assertEquals(
                ((IObject)firstMessage.getValue(new FieldName("session"))).getValue(new FieldName("targetField")),
                value);
    }



    SMObjectBuilder messageWithMapOf(IObject... targets) {
        return new SMObjectBuilder()
                .with("messageMap",new StraightMessageMap(Arrays.asList(targets)));
    }
}