import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.types.ObjectId;

public class CreateInDB {
    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create(BotConfig.DB_TOKEN)) {
            MongoDatabase database = mongoClient.getDatabase("MagistrmateDatabase");
            MongoCollection<Document> collection = database.getCollection("MagistrmateCollection");
            try {
                InsertOneResult result = collection.insertOne(new Document()
                        .append("_id", new ObjectId())
                        .append("name", "Параллельно задавая вопрос")
                        .append("cover", "AgACAgIAAxkBAAIBx2KTdP4CNbqTZfv7Hm7TqGAugkdSAAKIvjEbUaqZSPwL-Up482owAQADAgADeQADJAQ")
                        .append("description", """
                                Сборник из пяти произведений\\. Подробные аннотации внутри\\.
                                *Под покровом единства*
                                Существует вуз, где запрещена дружба, и всех обучают рассчитывать только на себя\\.
                                *Корректировка*
                                В прошлом случайный спор друзей из компании повлиял на их судьбу в настоящем\\.
                                *Лунные тени*
                                Хождение во сне жителей общаги и кровавые истории под утро\\.
                                *Звезда*
                                Сказка о том, как на Землю упала Звезда\\.
                                *Понимания ноль*
                                Дружба между парнем и девушкой существует и поддерживается государством, давая им невероятные способности\\.""")
                        .append("Shops", new Document()
                                .append("ridero", "https://ridero.ru/books/parallelno_zadavaya_vopros/")
                                .append("litres", "https://www.litres.ru/daniil-apasov/parallelno-zadavaya-vopros-pod-pokrovom-edinstva-korrektirov/")
                                .append("wildberries", "https://www.wildberries.ru/catalog/36734671/detail.aspx?targetUrl=SN")
                                .append("ozon", "https://www.ozon.ru/product/parallelno-zadavaya-vopros-168137107/?sh=qwZ99MK_wQ")
                                .append("aliexpress","https://aliexpress.ru/item/1005002349414876.html?gatewayAdapt=glo2rus&sku_id=12000020229783418")
                                .append("amazon","https://www.amazon.com/dp/B084Q3G56J")));
                System.out.println("Success! Inserted document id: " + result.getInsertedId());
            } catch (MongoException me) {
                System.err.println("Unable");
            }
        }
    }
}
