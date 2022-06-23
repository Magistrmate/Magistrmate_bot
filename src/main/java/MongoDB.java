import com.mongodb.client.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.all;
import static com.mongodb.client.model.Filters.eq;

public class MongoDB {
    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create(BotConfig.DB_TOKEN);
        MongoDatabase database = mongoClient.getDatabase("MagistrmateDatabase");
        MongoCollection<Document> collection = database.getCollection("MagistrmateCollection");
        List<Document> books = collection.find().into(new ArrayList<>());
        for (Document book : books) {
            System.out.println(book.getEmbedded(Arrays.asList("Shops", "ridero"), String.class));
        }
/*        Document book = collection.find().first();
        assert book != null;
        //System.out.println(book.getEmbedded(Arrays.asList("Shops", "ridero"), String.class));
        System.out.println(book.getEmbedded(List.of("Shops"), Document.class));*/
    }
}
