package actors.session;

import info.smart_tools.smartactors.core.*;
import info.smart_tools.smartactors.core.actors.Actor;
import info.smart_tools.smartactors.core.actors.annotations.Handler;
import info.smart_tools.smartactors.core.actors.service.ServiceActorFields;
import info.smart_tools.smartactors.core.actors.session.SessionFields;
import info.smart_tools.smartactors.core.addressing.AddressingFields;
import info.smart_tools.smartactors.core.addressing.maps.MessageMap;
import info.smart_tools.smartactors.core.addressing.targeting.Targets;
import info.smart_tools.smartactors.core.addressing.targeting.actors.ActorPath;
import info.smart_tools.smartactors.utils.ioc.IOC;

import java.util.*;

public class SessionActor extends Actor {

    /** Session storage */
    private Map<String,IObject> sessions = new HashMap<>();
    private String databaseActorPath;
    private String sessionActorPath;
    private String collectionName;
    private Map<Integer, String> dbNames;
    private Integer standartSessionIdLength = "9810efce-204e-4211-90d6-973e86b07d44".length();

    private static Field<Integer> idPartField = new Field<>(new FieldName("id"));
    private static Field<String> fullNameField = new Field<>(new FieldName("name"));
    private static Field<Long> idDocumentField = new Field<>(new FieldName("id"));
    private static Field<String> notEqualsField = new Field<>(new FieldName("$ne"));
    private static Field<String> equalsField = new Field<>(new FieldName("$eq"));
    private static Field<String> errorMessageField = new Field<>(new FieldName("errorMessage"));

    /**
     * Constructor by parameters.
     * @param conf contains
     *               1. "databaseActorPath": "databaseAccessActor" - actor path by which databaseAccessActor was connected
     *               2. "name":  "sessionActor" - actor path by which sessionActor was connected (standard name parameter)
     *               3. "collectionName" : "name_Of_BaseWithNames_Of_BaseWithSessions" - имя коллекции в БД,
     *                                   в которой будут храниться имена коллекций, содержащих сессии
     */
    public SessionActor (IObject conf) {
        try {
            dbNames = new HashMap<>();
            collectionName = ServiceActorFields.COLLECTION_NAME_FIELD.from(conf, String.class);
            databaseActorPath = ServiceActorFields.DATABASE_ACTOR_PATH_FIELD.from(conf, String.class) == null ?
                "databaseAccessActor" :
                ServiceActorFields.DATABASE_ACTOR_PATH_FIELD.from(conf, String.class);
            sessionActorPath = ServiceActorFields.NAME_FIELD.from(conf, String.class) == null ?
                "sessionActor" :
                ServiceActorFields.NAME_FIELD.from(conf, String.class);
        } catch (ReadValueException | ChangeValueException e) {
            //TODO:: handle
        }
    }

    @Handler("search")
    //TODO:: Handles Exception
    public void afterSearchNamesInDB(SessionMessage message) throws Exception{
        message.setPageNumber(1);
        message.setPageSize(100);
        message.setCollectionName(collectionName);
        System.out.println(collectionName);

        IObject idObject = IOC.resolve(IObject.class);
        IObject equalsObject = IOC.resolve(IObject.class);
        notEqualsField.inject(equalsObject, null);
        actors.session.SessionFields.ID_FIELD.inject(idObject, equalsObject);
        message.setQuery(idObject);
    }

    @Handler("load")
    public void loadNames(SessionMessage message) throws ReadValueException{
        List<IObject> names = message.getSearchResult();

        if (names == null || names.size() == 0)
            throw new ReadValueException("db " + collectionName + " is empty");

        for (IObject iObject : names){
            try {
                Integer id = idPartField.from(iObject, Integer.class);
                String dbName = fullNameField.from(iObject, String.class);
                System.out.println(id + ":" + dbName);
                dbNames.put(id, dbName);
            } catch (ReadValueException | ChangeValueException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets session from RAM or DB according to sessionId, or creates new session and sessionId
     * @param message the message
     * @throws ReadValueException
     * @throws ChangeValueException
     */
    @Handler("getSession")
    public void getSessionHandler(SessionMessage message) throws ReadValueException, ChangeValueException {
        String sessionId = message.getSessionId();
        IObject sessionObject;

        //creates new sessionId, session and puts it in RAM
        if(sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            sessionId = modifySessionId(sessionId);
            sessionObject = IOC.resolve(IObject.class);
            actors.session.SessionFields.SESSION_ID_STRING_FIELD.inject(sessionObject, sessionId);
            sessions.put(sessionId, sessionObject);
            message.setSessionId(sessionId);
        //not found in RAM, search in DB
        } else {
            sessionObject = sessions.get(sessionId);

            if (sessionObject == null) {
                buildSessionSearchQuery(message, "handleSessionFromDB", sessionId);
            }
        }

        message.setSession(sessionObject);

        respondOn(message, response -> {
        });
    }

    /**
     * Handles sessions from DB. It's impossible to cause directly, because searchResult can be empty.
     * @param message the message which passed {@Link DatabaseAccessActor} with handler("find-documents")
     * @throws ReadValueException
     * @throws ChangeValueException
     */
    @Handler("handleSessionFromDB")
    public void handleSessionFromDB(SessionMessage message) throws ReadValueException, ChangeValueException {
        List<IObject> sessionsFromDB = message.getSearchResult();
        if (sessionsFromDB == null || sessionsFromDB.isEmpty()) {
            MessageMap messageMap = AddressingFields.MESSAGE_MAP_FIELD.from(message.extractWrapped(), MessageMap.class);
            IObject exchanging = IOC.resolve(IObject.class);
            AddressingFields.TARGET_FIELD.inject(exchanging, ActorPath.fromString("exchanging"));
            IObject finish = IOC.resolve(IObject.class);
            AddressingFields.TARGET_FIELD.inject(finish, Targets.FINISHED_MAP);

            messageMap.insert(Arrays.asList(exchanging, finish));
            respondOn(message, response -> {
                //TODO:: localized error message
                errorMessageField.inject(response, "Session id is invalid");
            });
            return;
        }
        IObject session = sessionsFromDB.get(0);
        sessions.put(actors.session.SessionFields.SESSION_ID_STRING_FIELD.from(session, String.class), session);
        message.setSession(session);
        respondOn(message, response -> {});
    }

    @Handler("saveSession")
    public void saveSession(SessionMessage message) throws ReadValueException, ChangeValueException {
        IObject session = this.sessions.get(message.getSessionId());

        if(session == null) {
            // If session not found in RAM - try to find it in DB
            buildSessionSearchQuery(message, "upsertSession", actors.session.SessionFields.SESSION_ID_STRING_FIELD.from(message.getSession(), String.class));
            message.setDocuments(Collections.singletonList(message.getSession()));
        } else {
            // Else - just save the exist session to DB
            upsertSession(message, session);
        }

        respondOn(message, response -> {});
    }

    /**
     * Handles response from DB actor with found in DB session(s) when saving session
     *
     * @param message the message
     * @throws ReadValueException
     * @throws ChangeValueException
     */
    @Handler("upsertSession")
    public void upsertSession(SessionMessage message) throws ReadValueException, ChangeValueException {
        List<IObject> foundSessions = message.getSearchResult();
        String sessionId = message.getSessionId();
        IObject session = this.sessions.get(sessionId);

        if(foundSessions.size() == 1) {
            IObject sessionFromDB = foundSessions.get(0);
            IObjectUtils.assign(sessionFromDB, session);
            session = sessionFromDB;
            message.setSession(session);
            this.sessions.put(sessionId, session);

            upsertSession(message, session);
        }
        respondOn(message, response -> {});
    }

    /**
     * Builds an query to database actor to update or insert session.
     *
     * @param message message where to put query
     * @param session session to save in DB
     * @throws ReadValueException
     * @throws ChangeValueException
     */
    private void upsertSession(SessionMessage message, IObject session) throws ReadValueException, ChangeValueException {
        MessageMap messageMap = AddressingFields.MESSAGE_MAP_FIELD.from(message.extractWrapped(), MessageMap.class);
        IObject actorDBTarget = IOC.resolve(IObject.class);
        AddressingFields.TARGET_FIELD.inject(actorDBTarget, ActorPath.fromString(databaseActorPath));

        if(idDocumentField.from(session, Long.class) == null) {
            AddressingFields.HANDLER_FIELD.inject(actorDBTarget, ActorPath.fromString("insert-documents"));
        } else {
            AddressingFields.HANDLER_FIELD.inject(actorDBTarget, ActorPath.fromString("update-documents"));
        }

        messageMap.insert(Collections.singletonList(actorDBTarget));
        message.setDocuments(Collections.singletonList(session));
        message.setCollectionName(extractNameOfDBFromSessionId(message.getSessionId()));
    }

    @Handler("setSessionField")
    public void setSessionField(SessionMessage message) throws ReadValueException, ChangeValueException {
        IObject sessionObject = sessions.get(message.getSessionId());
        IObject currentTarget = AddressingFields.MESSAGE_MAP_FIELD.from(message.extractWrapped(), MessageMap.class).getPreviousTarget();
        IObject targetParameters = AddressingFields.PARAMETERS_FIELD.from(currentTarget, IObject.class);

        NestedField<Object> sourceField = new NestedField<>(actors.session.SessionFields.FROM_FIELD.from(targetParameters, FieldName.class));
        NestedField<Object> destinationField = new NestedField<>(actors.session.SessionFields.TO_FIELD.from(targetParameters, FieldName.class));

        destinationField.inject(sessionObject, sourceField.from(message.extractWrapped(), Object.class));

        respondOn(message, msg -> {});
    }

    /**
     * Insert database number in sessionId
     * @param sessionId sessionId value from session object
     */
    private String modifySessionId(String sessionId){
        if (sessionId == null || sessionId.equals("")){
            throw new IllegalArgumentException("Invalid session id");
        }

        Object[] ids = dbNames.keySet().toArray();
        Integer numberOfDB = (int)(Math.random() * ids.length);
        //Integer numberOfDB = dbNames.entrySet().stream().findAny().get().getKey();
        return (sessionId + ids[numberOfDB]);
    }

    /**
     * Extract name of database from sessionId
     * @param sessionId sessionId value from session object
     * @return String name of DB
     */
    private String extractNameOfDBFromSessionId(String sessionId){
        //Integer numberOfDB = Integer.parseInt(sessionId.substring(0, (databasesCount + "").length()));
        Integer numberOfDB = Integer.parseInt(sessionId.substring(standartSessionIdLength, sessionId.length()));

        String curDatabase = dbNames.get(numberOfDB);
        if (curDatabase == null || curDatabase.equals(""))
            throw new IllegalArgumentException("Not found db");

        return (dbNames.get(numberOfDB));
    }

    /**
     * Method for construct query for search by id and set to message map next targets
     * @param message session message
     * @param handlerName handler name
     * @param sessionId sessionId value from session object
     * @throws ReadValueException
     * @throws ChangeValueException
     */
    private void buildSessionSearchQuery(SessionMessage message, String handlerName, String sessionId)
        throws ReadValueException, ChangeValueException {
        MessageMap messageMap = AddressingFields.MESSAGE_MAP_FIELD.from(message.extractWrapped(), MessageMap.class);
        IObject actorDBTarget = IOC.resolve(IObject.class);
        AddressingFields.TARGET_FIELD.inject(actorDBTarget, ActorPath.fromString(databaseActorPath));
        AddressingFields.HANDLER_FIELD.inject(actorDBTarget, ActorPath.fromString("find-documents"));
        IObject sessionActorTarget = IOC.resolve(IObject.class);
        AddressingFields.TARGET_FIELD.inject(sessionActorTarget, ActorPath.fromString(sessionActorPath));
        AddressingFields.HANDLER_FIELD.inject(sessionActorTarget, ActorPath.fromString(handlerName));
        messageMap.insert(Arrays.asList(actorDBTarget, sessionActorTarget));

        IObject idObject = IOC.resolve(IObject.class);
        IObject equalsObject = IOC.resolve(IObject.class);
        message.setPageNumber(1);
        message.setPageSize(1);
        equalsField.inject(equalsObject, sessionId);
        actors.session.SessionFields.SESSION_ID_IOBJECT_FIELD.inject(idObject, equalsObject);
        message.setQuery(idObject);
        message.setCollectionName(extractNameOfDBFromSessionId(sessionId));
    }
}
