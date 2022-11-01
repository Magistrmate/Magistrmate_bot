import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
public class MongoDB {
    public static void main(String[] args) {
        //        MongoCollection<Document> collection = database.getCollection("MagistrmateCollection");
        //        List<Document> books = collection.find().into(new ArrayList<>());
        //        for (Document book : books) {
        //            System.out.println(book.getEmbedded(Arrays.asList("Shops", "ridero"), String.class));
        //        }
        //        myMap.put("Wildberries", "wildberries");
        //        myMap.put("Ozon", "ozon");
        //        myMap.put("AliExpress", "aliexpress");
        //        myMap.put("Amazon", "amazon");
        //        for(Collection<String> value : map.asMap().values()) {
        //            System.out.println(value.iterator().next().lines().);
        //            System.out.println(value.iterator().next());
        //        }
        /*kjb        Document book = collection.find().first();
                assert book != null;
                //System.out.println(book.getEmbedded(Arrays.asList("Shops", "ridero"), String.class));
                System.out.println(book.getEmbedded(List.of("Shops"), Document.class));*/
        //collectionLog.find().batchSize(1000).forEach((Consumer<? super Document>) collectionLog2::insertOne);
        MongoClient mongoClient = MongoClients.create(BotConfig.DB_TOKEN);
        MongoDatabase databaseLog = mongoClient.getDatabase("MagistrmateDatabase");
        MongoCollection<Document> collectionLog = databaseLog.getCollection("Log");
        List<Document> books = collectionLog.find().into(new ArrayList<>());
        for (Document book : books) {
            System.out.println(book.getString("_id"));
            Document query = new Document().append("_id", book.getString("_id"));
            Bson updates = Updates.combine(Updates.set("NumberBook" , 1));
            UpdateOptions options = new UpdateOptions().upsert(true);
            collectionLog.updateOne(query, updates, options);
        }
    }
}