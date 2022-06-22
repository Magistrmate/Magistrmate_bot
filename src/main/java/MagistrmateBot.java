import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MagistrmateBot extends TelegramLongPollingBot {
    public static final String PZV_RIDERO = "ridero.ru/books/parallelno_zadavaya_vopros/";
    public static final String PZV_LITRES = "litres.ru/daniil-apasov/parallelno-zadavaya-vopros-pod-pokrovom-edinstva-korrektirov/";
    public static final String PZV_WILDBERRIES = "wildberries.ru/catalog/36734671/detail.aspx?targetUrl=SN";
    public static final String PZV_OZON = "ozon.ru/product/parallelno-zadavaya-vopros-168137107/?sh=qwZ99MK_wQ";
    public static final String PZV_ALIEXPRESS = "aliexpress.ru/item/1005002349414876.html?gatewayAdapt=glo2rus&sku_id=12000020229783418";
    public static final String PZV_AMAZON = "amazon.com/dp/B084Q3G56J";
    Integer Book = 1;
    Integer PreviousBook = 2;
    Integer nextBook = 0;
    Integer i = 1;
    String id;
    boolean next = false;

    @Override
    public String getBotUsername() {
        return "Magistrmate_bot";
    }

    @Override
    public String getBotToken() {
        return BotConfig.BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        MongoClient mongoClient = MongoClients.create(BotConfig.DB_TOKEN);
        MongoDatabase database = mongoClient.getDatabase("MagistrmateDatabase");
        MongoCollection<Document> collection = database.getCollection("MagistrmateCollection");
        Message message = update.getMessage();
        if (update.hasMessage()) {
            String text = message.getText().toLowerCase(Locale.ROOT);
            if (text.equals("/start")) {
                createMessage(message, "Добро пожаловать " + message.getFrom().getFirstName() + "\\!\n" +
                        "Мы можем перейти сразу к книгам или пообщаться\\. Я пока в процессе познания вашего мира," +
                        " поэтому пишите и если не пойму, то выдам вам подсказки\\.");
            } else if (text.contains("привет")) {
                createMessage(message, "Дороу");
            } else if (text.contains("книг") || text.contains("книж")) {
                createFewCovers(message, collection);
                createCover(update, message, database, collection);
            } else {
                createMessage(message, "Давайте вместе разберемся, чем я могу помочь");
            }
        } else if (update.hasCallbackQuery()) {
            Message backMessage = update.getCallbackQuery().getMessage();
            String backText = update.getCallbackQuery().getData();
            if (backText.equals("NextBook") || backText.equals("PreviousBook")) {
                if (backText.equals("PreviousBook")) {
                    if (nextBook == 1) nextBook = 5;
                    else nextBook = nextBook - 2;
                }
                if (nextBook == collection.countDocuments()) nextBook = 0;
                Document book = collection.find().skip(nextBook).first();
                InputMedia photo = new InputMediaPhoto();
                assert book != null;
                photo.setMedia(book.getString("cover"));
                photo.setCaption("*" + book.getString("name") + "*\n" + book.getString("description"));
                photo.setParseMode(ParseMode.MARKDOWNV2);
                EditMessageMedia replacePhoto = new EditMessageMedia();
                replacePhoto.setMedia(photo);
                replacePhoto.setChatId(backMessage.getChatId().toString());
                replacePhoto.setMessageId(Integer.valueOf(backMessage.getMessageId().toString()));
                InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
                createFirstKeyboard(update, inlineKeyboard, database);
                replacePhoto.setReplyMarkup(inlineKeyboard);
                try {
                    execute(replacePhoto);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (update.getCallbackQuery().getData().equals("12345")) {

            } else if (update.getCallbackQuery().getData().equals("ExcerptBook")) {
                System.out.println("EPUB");
                System.out.println("FB2");
                System.out.println("PDF");
                System.out.println("Online");
                System.out.println("Текст в картинках");
            } else if (update.getCallbackQuery().getData().equals("ShopsBook")) {
                EditMessageReplyMarkup keyboard = new EditMessageReplyMarkup();
                keyboard.setChatId(backMessage.getChatId().toString());
                keyboard.setMessageId(Integer.valueOf(backMessage.getMessageId().toString()));
                InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
                InlineKeyboardButton RideroButton = new InlineKeyboardButton();
                RideroButton.setText("Ridero");
                RideroButton.setUrl(PZV_RIDERO);
                InlineKeyboardButton LitResButton = new InlineKeyboardButton();
                LitResButton.setText("ЛитРес");
                LitResButton.setUrl(PZV_LITRES);
                InlineKeyboardButton WildberriesButton = new InlineKeyboardButton();
                WildberriesButton.setText("Wildberries");
                WildberriesButton.setUrl(PZV_WILDBERRIES);
                InlineKeyboardButton OzonButton = new InlineKeyboardButton();
                OzonButton.setText("Ozon");
                OzonButton.setUrl(PZV_OZON);
                InlineKeyboardButton AliExpressButton = new InlineKeyboardButton();
                AliExpressButton.setText("AliExpress");
                AliExpressButton.setUrl(PZV_ALIEXPRESS);
                InlineKeyboardButton AmazonButton = new InlineKeyboardButton();
                AmazonButton.setText("Amazon");
                AmazonButton.setUrl(PZV_AMAZON);
                InlineKeyboardButton inlineKeyboardButton7 = new InlineKeyboardButton();
                inlineKeyboardButton7.setText("Вернуться");
                inlineKeyboardButton7.setCallbackData("BackShopsBook");
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                row1.add(RideroButton);
                List<InlineKeyboardButton> row2 = new ArrayList<>();
                row2.add(LitResButton);
                row2.add(WildberriesButton);
                row2.add(OzonButton);
                List<InlineKeyboardButton> row3 = new ArrayList<>();
                row3.add(AliExpressButton);
                row3.add(AmazonButton);
                List<InlineKeyboardButton> row4 = new ArrayList<>();
                row4.add(inlineKeyboardButton7);
                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                rowList.add(row1);
                rowList.add(row2);
                rowList.add(row3);
                rowList.add(row4);
                inlineKeyboard.setKeyboard(rowList);
                keyboard.setReplyMarkup(inlineKeyboard);
                try {
                    execute(keyboard);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (update.getCallbackQuery().getData().equals("BackShopsBook")) {
                EditMessageReplyMarkup backKeyboard = new EditMessageReplyMarkup();
                backKeyboard.setChatId(backMessage.getChatId().toString());
                backKeyboard.setMessageId(Integer.valueOf(backMessage.getMessageId().toString()));
                InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
                createFirstKeyboard(update, inlineKeyboard, database);
                backKeyboard.setReplyMarkup(inlineKeyboard);
                try {
                    execute(backKeyboard);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createMessage(Message message, String text) {
        String user_first_name = message.getChat().getFirstName();
        String user_last_name = message.getChat().getLastName();
        String user_username = message.getChat().getUserName();
        long user_id = message.getChat().getId();
        String message_text = message.getText();
        log(user_first_name, user_last_name, user_username, Long.toString(user_id), message_text, text);

        SendMessage createMessage = new SendMessage();
        createMessage.setChatId(message.getChatId().toString());
        createMessage.setText(text);
        createMessage.enableMarkdownV2(true);

        if (text.equals("Давайте вместе разберемся, чем я могу помочь")) {
            createKeyboard(createMessage);
        }

        try {
            execute(createMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void createKeyboard(SendMessage createMessage) {
        ReplyKeyboardMarkup createKeyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Книги");
        row1.add("Аудиокниги");
        keyboard.add(row1);
        KeyboardRow row2 = new KeyboardRow();
        row2.add("Контакты");
        row2.add("Позвать оператора");
        keyboard.add(row2);
        createKeyboard.setKeyboard(keyboard);
        createKeyboard.setResizeKeyboard(true);
        createKeyboard.setOneTimeKeyboard(true);
        createKeyboard.setInputFieldPlaceholder("Общение");
        createKeyboard.setSelective(true); //https://core.telegram.org/bots/api#replykeyboardmarkup
        createMessage.setReplyMarkup(createKeyboard);
    }

    private void createFewCovers(Message message, MongoCollection<Document> collection) {

        List<InputMedia> media = new ArrayList<>();

        List<Document> books = collection.find().skip(1).into(new ArrayList<>());
        for (Document book : books) {
            InputMedia photo = new InputMediaPhoto();
            photo.setParseMode(ParseMode.MARKDOWNV2);
            photo.setMedia(book.getString("cover"));
            photo.setCaption("*" + book.getString("name") + "*\n" + book.getString("description"));
            media.add(photo);
        }
        SendMediaGroup mediaGroup = new SendMediaGroup();
        mediaGroup.setChatId(message.getChatId().toString());
        mediaGroup.setMedias(media);
        try {
            execute(mediaGroup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void createCover(Update update, Message message, MongoDatabase database, MongoCollection<Document> collection) {
        SendPhoto photo = new SendPhoto();
        photo.setParseMode(ParseMode.MARKDOWNV2);
        photo.setChatId(message.getChatId().toString());
        Document doc = collection.find().limit(1).first();
        assert doc != null;
        photo.setPhoto(new InputFile(doc.getString("cover")));
        photo.setCaption("*" + doc.getString("name") + "*\n" + doc.getString("description"));
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        createFirstKeyboard(update, inlineKeyboard, database);
        photo.setReplyMarkup(inlineKeyboard);
        try {
            execute(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void createFirstKeyboard(Update update, InlineKeyboardMarkup inlineKeyboard, MongoDatabase database) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        InlineKeyboardButton ShopsBookButton = new InlineKeyboardButton();
        ShopsBookButton.setText("Книга в магазинах");
        ShopsBookButton.setCallbackData("ShopsBook");
        InlineKeyboardButton NextBookButton = new InlineKeyboardButton();
        NextBookButton.setText("Следующая книга");
        InlineKeyboardButton ExcerptBookButton = new InlineKeyboardButton();
        NextBookButton.setCallbackData("NextBook");
        ExcerptBookButton.setText("Отрывок из книги");
        ExcerptBookButton.setCallbackData("ExcerptBook");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(ShopsBookButton);
        rowList.add(row1);
        List<InlineKeyboardButton> row2_3 = new ArrayList<>();
        nextBook++;
        if (update.getCallbackQuery() != null) {
            List<InlineKeyboardButton> row2 = new ArrayList<>();
            MongoCollection<Document> collection = database.getCollection("MagistrmateCollection");
            for (int i = 1; i <= collection.countDocuments(); i++) {
                InlineKeyboardButton bookButton = new InlineKeyboardButton();
                if (nextBook == i) bookButton.setText("• " + i + " •");
                else bookButton.setText(String.valueOf(i));
                bookButton.setCallbackData(String.valueOf(i));
                row2.add(bookButton);
            }
            rowList.add(row2);
            InlineKeyboardButton PreviousBookButton = new InlineKeyboardButton();
            PreviousBookButton.setText("Предыдущая книга");
            PreviousBookButton.setCallbackData("PreviousBook");
            row2_3.add(PreviousBookButton);
        }
        row2_3.add(NextBookButton);
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(ExcerptBookButton);
        rowList.add(row2_3);
        rowList.add(row3);
        inlineKeyboard.setKeyboard(rowList);
    }

    private void log(String first_name, String last_name, String user_username, String user_id, String txt, String bot_answer) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println(first_name + " " + last_name + " (" + user_id + " " + user_username + ")\n" + txt);
        System.out.println("Magistrmate Bot\n" + bot_answer);
    }

}