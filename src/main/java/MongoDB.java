import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.function.Consumer;
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
        MongoClient mongoClient = MongoClients.create(BotConfig.DB_TOKEN);
        MongoDatabase databaseLog = mongoClient.getDatabase("Log");
        MongoDatabase databaseLog2 = mongoClient.getDatabase("MagistrmateDatabase");
        MongoCollection<Document> collectionLog = databaseLog.getCollection("Log");
        MongoCollection<Document> collectionLog2 = databaseLog2.getCollection("Log");
        collectionLog.find().batchSize(1000).forEach((Consumer<? super Document>) collectionLog2::insertOne);
    }
}