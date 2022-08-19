import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;

public class CreateInLogDB {
    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create(BotConfig.DB_TOKEN)) {
            MongoDatabase database = mongoClient.getDatabase("Log");
            MongoCollection<Document> collection = database.getCollection("Log");
            try {
                InsertOneResult result = collection.insertOne(new Document()
                        .append("_id", "Userid")
                        .append("name", "Понимания ноль")
                        .append("cover", "AgACAgIAAxkBAAICI2KV6u5ALDMcSPP4WPsvdr5iBJ1hAALGuTEbgvKxSGlGhmGbA1qtAQADAgADeQADJAQ")
                        .append("description", "Привет\\. Я тут рассказал неординарную историю нашего путешествия и немного о нашем мире\\. Представляете, у нас дружба между парнем и девушкой возможна\\. Причем на законодательном уровне\\. Мы начинаем обладать особыми силами, но это уже подробнее внутри\\. Возможно, ваша жизненная ситуация похожа на нашу, и вы являетесь таким другом противоположному себе полу, а?")
                        .append("Shops", new Document()
                                .append("ridero", "ridero.ru/books/ponimaniya_nol/")
                                .append("litres", "litres.ru/daniil-apasov/ponimaniya-nol-parallelno-zadavaya-vopros/")
                                .append("wildberries", "wildberries.ru/catalog/36734665/detail.aspx")
                                .append("ozon", "ozon.ru/context/detail/id/168137116/")
                                .append("aliexpress", "aliexpress.com/item/1005002349453670.html")
                                .append("amazon", "amazon.com/dp/B084Q88C9B")));
                System.out.println("Success! Inserted document id: " + result.getInsertedId());
            } catch (MongoException me) {
                System.err.println("Unable");
            }
        }
    }
}
