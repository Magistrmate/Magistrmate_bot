import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoDB {
    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create(BotConfig.DB_TOKEN);
//        MongoDatabase database = mongoClient.getDatabase("MagistrmateDatabase");
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
/*        Document book = collection.find().first();
        assert book != null;
        //System.out.println(book.getEmbedded(Arrays.asList("Shops", "ridero"), String.class));
        System.out.println(book.getEmbedded(List.of("Shops"), Document.class));*/
    }
}