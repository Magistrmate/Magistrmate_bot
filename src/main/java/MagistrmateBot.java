import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
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
import java.util.*;

public class MagistrmateBot extends TelegramLongPollingBot {
    Integer nextBook = 0;
    Integer showBook;
    Boolean NextBook = false;

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
            if (message.hasDocument()) {
                Document query = new Document().append( "_id",message.getCaption());
                Bson updates = Updates.combine(Updates.set("pdf", update.getMessage().getDocument().getFileId()));
                UpdateOptions options = new UpdateOptions().upsert(true);
                try {
                    collection.updateOne(query,updates,options);
                } catch (MongoException me) {
                    System.err.println("Unable" + me);
                }
            } else {
                String text = message.getText().toLowerCase(Locale.ROOT);
                if (text.equals("/start")) {
                    createMessage(message, "Добро пожаловать " + message.getFrom().getFirstName() + "\\!\n" +
                            "Мы можем перейти сразу к книгам или пообщаться\\. Я пока в процессе познания вашего мира," +
                            " поэтому пишите и если не пойму, то выдам вам подсказки\\.");
                } else if (text.contains("привет")) {
                    createMessage(message, "Дороу");
                } else if (text.contains("книг") || text.contains("книж")) {
                    createFewCovers(message, collection);
                    createCover(update, message, collection);
                } else {
                    createMessage(message, "Давайте вместе разберемся, чем я могу помочь");
                }
            }
        } else if (update.hasCallbackQuery()) {
            Message backMessage = update.getCallbackQuery().getMessage();
            String backText = update.getCallbackQuery().getData();
            showBook = nextBook - 1;
            if (backText.equals("NextBook") || backText.equals("PreviousBook") || backText.matches(".*\\d+.*")) {
                NextBook = true;
                if (backText.equals("PreviousBook")) {
                    if (nextBook == 1) nextBook = 5;
                    else nextBook = nextBook - 2;
                } else if (backText.matches(".*\\d+.*")) {
                    nextBook = Integer.parseInt(backText) - 1;
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
                nextBook++;
                createFirstKeyboard(update, inlineKeyboard, collection);
                replacePhoto.setReplyMarkup(inlineKeyboard);
                try {
                    execute(replacePhoto);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (backText.equals("ExcerptBook")) {
                Document book = collection.find().skip(showBook).first();
                assert book != null;
                EditMessageReplyMarkup keyboard = new EditMessageReplyMarkup();
                keyboard.setChatId(backMessage.getChatId().toString());
                keyboard.setMessageId(Integer.valueOf(backMessage.getMessageId().toString()));
                InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                List<InlineKeyboardButton> row2 = new ArrayList<>();
                List<InlineKeyboardButton> row3 = new ArrayList<>();
                List<InlineKeyboardButton> row4 = new ArrayList<>();
                InlineKeyboardButton button1 = new InlineKeyboardButton();
                InlineKeyboardButton button2 = new InlineKeyboardButton();
                InlineKeyboardButton button3 = new InlineKeyboardButton();
                InlineKeyboardButton button4 = new InlineKeyboardButton();
                InlineKeyboardButton button5 = new InlineKeyboardButton();
                InlineKeyboardButton returnButton = new InlineKeyboardButton();
                button1.setText("Online");
                button1.setCallbackData("Online");
                button1.setUrl(book.getString("excerpt"));
                button2.setText("EPUB");
                button2.setCallbackData("EPUB");
                button3.setText("FB2");
                button3.setCallbackData("FBTwo");
                button4.setText("Текст в картинках");
                button4.setCallbackData("Текст в картинках");
                button5.setText("PDF");
                button5.setCallbackData("PDF");
                returnButton.setText("Вернуться");
                returnButton.setCallbackData("BackShopsBook");
                row1.add(button1);
                row2.add(button2);
                row2.add(button3);
                row3.add(button4);
                row3.add(button5);
                row4.add(returnButton);
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
            } else if (update.getCallbackQuery().getData().equals("EPUB")) {
                createDocument(backMessage, collection, update.getCallbackQuery().getData());
            } else if (update.getCallbackQuery().getData().equals("FBTwo")) {
                createDocument(backMessage, collection, update.getCallbackQuery().getData());
            } else if (update.getCallbackQuery().getData().equals("PDF")) {
                createDocument(backMessage, collection, update.getCallbackQuery().getData());
            } else if (update.getCallbackQuery().getData().equals("ShopsBook")) {
                Document book = collection.find().skip(showBook).first();
                assert book != null;
                EditMessageReplyMarkup keyboard = new EditMessageReplyMarkup();
                keyboard.setChatId(backMessage.getChatId().toString());
                keyboard.setMessageId(Integer.valueOf(backMessage.getMessageId().toString()));
                InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                List<InlineKeyboardButton> row2 = new ArrayList<>();
                List<InlineKeyboardButton> row3 = new ArrayList<>();
                List<InlineKeyboardButton> row4 = new ArrayList<>();
                InlineKeyboardButton button1 = new InlineKeyboardButton();
                InlineKeyboardButton button2 = new InlineKeyboardButton();
                InlineKeyboardButton button3 = new InlineKeyboardButton();
                InlineKeyboardButton button4 = new InlineKeyboardButton();
                InlineKeyboardButton button5 = new InlineKeyboardButton();
                InlineKeyboardButton button6 = new InlineKeyboardButton();
                InlineKeyboardButton returnButton = new InlineKeyboardButton();
                urlShops("Ridero", "ridero", button1, book, row1);
                urlShops("ЛитРес", "litres", button2, book, row2);
                urlShops("Wildberries", "wildberries", button3, book, row2);
                urlShops("Ozon", "ozon", button4, book, row2);
                urlShops("AliExpress", "aliexpress", button5, book, row3);
                urlShops("Amazon", "amazon", button6, book, row3);
                returnButton.setText("Вернуться");
                returnButton.setCallbackData("BackShopsBook");
                row4.add(returnButton);
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
            } else if (backText.equals("BackShopsBook")) {
                EditMessageReplyMarkup backKeyboard = new EditMessageReplyMarkup();
                backKeyboard.setChatId(backMessage.getChatId().toString());
                backKeyboard.setMessageId(Integer.valueOf(backMessage.getMessageId().toString()));
                InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
                createFirstKeyboard(update, inlineKeyboard, collection);
                backKeyboard.setReplyMarkup(inlineKeyboard);
                try {
                    execute(backKeyboard);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void urlShops(String text, String value, InlineKeyboardButton button, Document book,
                         List<InlineKeyboardButton> row) {
        button.setText(text);
        button.setUrl(book.getEmbedded(Arrays.asList("Shops", value), String.class));
        row.add(button);
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

    private void createCover(Update update, Message message, MongoCollection<Document> collection) {
        SendPhoto photo = new SendPhoto();
        photo.setParseMode(ParseMode.MARKDOWNV2);
        photo.setChatId(message.getChatId().toString());
        Document doc = collection.find().limit(1).first();
        assert doc != null;
        photo.setPhoto(new InputFile(doc.getString("cover")));
        photo.setCaption("*" + doc.getString("name") + "*\n" + doc.getString("description"));
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        nextBook++;
        createFirstKeyboard(update, inlineKeyboard, collection);
        photo.setReplyMarkup(inlineKeyboard);
        try {
            execute(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void createDocument(Message message, MongoCollection<Document> collection, String whichButton) {
        String smallLetter = whichButton.toLowerCase(Locale.ROOT);
        SendDocument document = new SendDocument();
        document.setChatId(message.getChatId().toString());
        Document doc = collection.find().skip(showBook).first();
        assert doc != null;
        document.setDocument(new InputFile(doc.getString(smallLetter)));
        try {
            execute(document);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void createFirstKeyboard(Update update, InlineKeyboardMarkup inlineKeyboard, MongoCollection<Document> collection) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        InlineKeyboardButton ShopsButton = new InlineKeyboardButton();
        InlineKeyboardButton NextButton = new InlineKeyboardButton();
        InlineKeyboardButton ExcerptButton = new InlineKeyboardButton();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2_3 = new ArrayList<>();
        List<InlineKeyboardButton> row3_4 = new ArrayList<>();
        ShopsButton.setText("Книга в магазинах");
        ShopsButton.setCallbackData("ShopsBook");
        NextButton.setText("Следующая книга");
        NextButton.setCallbackData("NextBook");
        ExcerptButton.setText("Отрывок из книги");
        ExcerptButton.setCallbackData("ExcerptBook");
        row1.add(ShopsButton);
        rowList.add(row1);
        if (update.getCallbackQuery() != null && NextBook) {
            List<InlineKeyboardButton> row2 = new ArrayList<>();
            for (int i = 1; i <= collection.countDocuments(); i++) {
                InlineKeyboardButton bookButton = new InlineKeyboardButton();
                if (nextBook == i) bookButton.setText("• " + i + " •");
                else bookButton.setText(String.valueOf(i));
                bookButton.setCallbackData(String.valueOf(i));
                row2.add(bookButton);
            }
            rowList.add(row2);
            InlineKeyboardButton PreviousButton = new InlineKeyboardButton();
            PreviousButton.setText("Предыдущая книга");
            PreviousButton.setCallbackData("PreviousBook");
            row2_3.add(PreviousButton);
        }
        row2_3.add(NextButton);
        row3_4.add(ExcerptButton);
        rowList.add(row2_3);
        rowList.add(row3_4);
        inlineKeyboard.setKeyboard(rowList);
    }

    private void createKeyboard(SendMessage createMessage) {
        ReplyKeyboardMarkup createKeyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add("Книги");
        row1.add("Аудиокниги");
        row2.add("Контакты");
        row2.add("Позвать оператора");
        keyboard.add(row1);
        keyboard.add(row2);
        createKeyboard.setKeyboard(keyboard);
        createKeyboard.setResizeKeyboard(true);
        createKeyboard.setOneTimeKeyboard(true);
        createKeyboard.setInputFieldPlaceholder("Общение");
        createKeyboard.setSelective(true); //https://core.telegram.org/bots/api#replykeyboardmarkup
        createMessage.setReplyMarkup(createKeyboard);
    }

    private void log(String first_name, String last_name, String user_username, String user_id, String txt, String bot_answer) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println(first_name + " " + last_name + " (" + user_id + " " + user_username + ")\n" + txt);
        System.out.println("Magistrmate Bot\n" + bot_answer);
    }

}