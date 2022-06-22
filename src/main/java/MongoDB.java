import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoDB {
    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create(BotConfig.DB_TOKEN);
        MongoDatabase database = mongoClient.getDatabase("MagistrmateDatabase");
        MongoCollection<Document> collection = database.getCollection("MagistrmateCollection");
        List<Document> docs = collection.find().into(new ArrayList<>());
        System.out.println(docs.get(0).toJson());
/*        for (Document student : docs) {
            System.out.println(student.toJson());
        }*/
    }
}
