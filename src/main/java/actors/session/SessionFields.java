package actors.session;

import info.smart_tools.smartactors.core.Field;
import info.smart_tools.smartactors.core.FieldName;
import info.smart_tools.smartactors.core.IObject;

/**
 * Fields and constants for working with session
 */
public class SessionFields {

    public final static String SESSION_ID = "sessionId";

    public static final Field<IObject> SESSION_FIELD = new Field<>(new FieldName("session"));
    public static final Field<String> COOKIES_FIELD = new Field<>(new FieldName("Cookie"));
    public static final Field<String> SESSION_ID_STRING_FIELD = new Field<>(new FieldName(SESSION_ID));
    public static final Field<IObject> SESSION_ID_IOBJECT_FIELD = new Field<>(new FieldName(SESSION_ID));
    public static final Field<IObject> ID_FIELD = new Field<>(new FieldName("id"));

    public static Field<FieldName> FROM_FIELD= new Field<>(new FieldName("from"));
    public static Field<FieldName> TO_FIELD = new Field<>(new FieldName("to"));

}
