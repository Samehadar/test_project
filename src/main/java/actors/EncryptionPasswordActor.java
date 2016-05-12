package actors;

import info.smart_tools.smartactors.core.*;
import info.smart_tools.smartactors.core.actors.Actor;
import info.smart_tools.smartactors.core.actors.annotations.Handler;
import messages.RegistrationInDBMessage;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Vitaly on 16.04.2016.
 */
public class EncryptionPasswordActor extends Actor {

    public EncryptionPasswordActor(IObject iObject){
        System.out.println(this.getClass().getName());
    }

    @Handler("Handle")
    public void handle(RegistrationInDBMessage message) throws ReadValueException, ChangeValueException, NoSuchAlgorithmException {

        String password = message.getPassword();

        if (password == null)
            throw new IllegalArgumentException("Incorrect password");

        if (password.length() < 6)
            throw new IllegalArgumentException("Short password");


        System.out.println(password);

        MessageDigest md5 = MessageDigest.getInstance("SHA-256");
        md5.update(password.getBytes(), 0, password.length());
        password = new BigInteger(1, md5.digest()).toString(16);

        System.out.println(password);
        message.setPassword(password);

        /*
        Field<String> f1 = new Field<>(new FieldName("username"));
        Field<String> f2 = new Field<>(new FieldName("password"));
        System.out.println(f1.from(message, String.class) + " : " + f2.from(message, String.class));
        */
    }

}
