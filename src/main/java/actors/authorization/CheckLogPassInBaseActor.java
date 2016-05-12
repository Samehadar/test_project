package actors.authorization;

import info.smart_tools.smartactors.core.IObject;
import info.smart_tools.smartactors.core.actors.Actor;
import info.smart_tools.smartactors.core.actors.annotations.Handler;
import messages.AuthorizationMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vitaly on 16.04.2016.
 */
public class CheckLogPassInBaseActor extends Actor {

    public CheckLogPassInBaseActor(IObject iObject){
        System.out.println(this.getClass().getName());
    }

    /*
    Create message:
    {
        "collectionName": "accounts",
        "pageSize": 100,
        "pageNumber": 1,
        "query": {
            "login": {"$eq": message.login},
            "password": {"$eq": message.password}
        }
    }
    */
    @Handler("Handle")
    public void handle(AuthorizationMessage message){
        String username = message.getUsername();
        String password = message.getPassword();

        Map<String, Map<String, String>> acc = new HashMap<>();

        //add check login
        Map<String, String> criterion = new HashMap<>();
        criterion.put("$eq", username);
        acc.put("login", criterion);

        //add check pass
        criterion = new HashMap<>();
        criterion.put("$eq", password);
        acc.put("password", criterion);

        message.setCollectionName("accounts");
        message.setPageSize(100);
        message.setPageNumber(1);
        message.setQuery(acc);
    }

}
