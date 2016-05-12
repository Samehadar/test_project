package actors;

import info.smart_tools.smartactors.core.*;
import info.smart_tools.smartactors.core.actors.Actor;
import info.smart_tools.smartactors.core.actors.annotations.Handler;
import messages.CreateDBMessage;

/**
 * Created by Vitaly on 13.04.2016.
 */
public class CreateDBActor extends Actor {

    public CreateDBActor(IObject iObject){
        System.out.println(this.getClass().getName());
    }

    @Handler("Handle")
    public void handle(CreateDBMessage message) throws Exception{
        System.out.println("Im work!");
        System.out.println("I'm new change");
        /*
        * For creating my test base with my params and my name for , uncomment text
        **/
        /*
        Map<String, String> indexes = new HashMap<>();
        indexes.put("id", "id");
        indexes.put("meta.tags", "tags");
        indexes.put("meta.creationate", "datetime");

        message.setIndexes(indexes);
        message.setCollectionName("mybase");
        */
    }


}
