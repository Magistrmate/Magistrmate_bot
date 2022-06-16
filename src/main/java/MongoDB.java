import com.mongodb.client.*;
import org.bson.Document;

public class MongoDB {
    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create(BotConfig.DB_TOKEN);
        MongoDatabase database = mongoClient.getDatabase("MagistrmateDatabase");
        MongoCollection<Document> collection = database.getCollection("MagistrmateCollection");
        Document document = new Document();
        FindIterable<Document> documentCursor = collection.find(document);
        for (Document doc : documentCursor) {
            System.out.println(doc.getString("_id"));
        }
    }
}
