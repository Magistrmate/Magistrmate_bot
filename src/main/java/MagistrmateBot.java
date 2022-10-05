import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.*;
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
    String textLog;
    String Id;
    String Info;
    String Answer;
    String Script;
    String BotLiveWithId = "";
    String messageGuest;
    String messageFrom;

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
            messageFrom = update.getMessage().getFrom().getId().toString();
            if (!BotLiveWithId.equals(messageGuest)) {
                messageGuest = update.getMessage().getFrom().getId().toString();
                if (message.hasAudio() || message.hasDocument()) {
                    Document query = new Document().append("_id", message.getCaption());
                    Bson updates = Updates.combine(Updates.set("audio", update.getMessage().getAudio().getFileId()));
                    UpdateOptions options = new UpdateOptions().upsert(true);
                    try {
                        collection.updateOne(query, updates, options);
                    } catch (MongoException me) {
                        System.err.println("Unable" + me);
                    }
                } else {
                    String text = message.getText().toLowerCase(Locale.ROOT);
                    createLog(update, mongoClient, text, "User", false);
                    if (text.equals("/start")) {
                        createMessage(message, "Добро пожаловать " + message.getFrom().getFirstName() + "\\!\n" +
                                "Мы можем перейти сразу к книгам или пообщаться\\. Я пока в процессе познания вашего " +
                                "мира, поэтому пишите и если не пойму, то выдам вам подсказки\\.", update, mongoClient);
                    } else if (text.contains("прив") || text.contains("хай")) {
                        createMessage(message, "Дороу", update, mongoClient);
                    } else if (text.toLowerCase(Locale.ROOT).contains("книг") ||
                            text.toLowerCase(Locale.ROOT).contains("книж")) {
                        createFewCovers(message, collection, update, mongoClient);
                        createCover(update, message, collection, mongoClient);
                    } else if (text.contains("оператор")) {
                        createMessage(message, "Ща свистну", update, mongoClient);
                        MongoDatabase databaseLog = mongoClient.getDatabase("Log");
                        MongoCollection<Document> collectionLog = databaseLog.getCollection("Log");
                        Document doc = collectionLog.find(Filters.eq("_id", Id)).first();
                        SendMessage createMessage = new SendMessage();
                        createMessage.setChatId(BotConfig.ID_SUPPORT);
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                        Date date = new Date();
                        assert doc != null;
                        createMessage.setText(doc.getString(dateFormat.format(date)));
                        createMessage.enableMarkdownV2(false);
                        BotLiveWithId = messageGuest;
                        try {
                            execute(createMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else {
                        createMessage(message, "Давайте вместе разберемся, чем я могу помочь", update,
                                mongoClient);
                    }
                }
            } else {
                if (messageFrom.equals(BotConfig.ID_SUPPORT) && message.getText().contains("До свидания")) {
                    BotLiveWithId = "";
                }
                SendMessage createMessage = new SendMessage();
                if (messageFrom.equals(BotConfig.ID_SUPPORT)) {
                    messageFrom = messageGuest;
                } else messageFrom = BotConfig.ID_SUPPORT;
                createMessage.setChatId(messageFrom);
                createMessage.setText(message.getText());
                try {
                    execute(createMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
        if (update.hasCallbackQuery()) {
            Message backMessage = update.getCallbackQuery().getMessage();
            String backText = update.getCallbackQuery().getData();
            createLog(update, mongoClient, "*Нажал на кнопку " + backText + "*", "User", true);
            showBook = nextBook - 1;
            if (backText.equals("next") || backText.equals("previous") || backText.matches(".*\\d+.*")) {
                NextBook = true;
                if (backText.equals("previous")) {
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
                    createLog(update, mongoClient, "*Перелистнул книгу*", "Bot ", true);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (backText.equals("excerpt")) {
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
                button1.setCallbackData("online");
                button1.setUrl(book.getString("excerpt"));
                button2.setText("EPUB");
                button2.setCallbackData("epub");
                button3.setText("FB2");
                button3.setCallbackData("fb-two");
                button4.setText("PDF");
                button4.setCallbackData("pdf");
                button5.setText("Аудио");
                button5.setCallbackData("audio");
                returnButton.setText("Вернуться");
                returnButton.setCallbackData("return");
                row1.add(button1);
                row2.add(button2);
                row2.add(button3);
                row2.add(button4);
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
                    createLog(update, mongoClient, "*Поменял клавиатуру на отрывки*", "Bot ", true);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (update.getCallbackQuery().getData().equals("epub")) {
                createDocument(backMessage, collection, update.getCallbackQuery().getData(), update, mongoClient,
                        backText);
                /* ok
                AnswerCallbackQuery answer = new AnswerCallbackQuery();
                String idCall = update.getCallbackQuery().getId();
                answer.setCallbackQueryId(idCall);
                answer.setText("piska");
                answer.setShowAlert(false);
                try{
                    execute(answer);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                */
            } else if (update.getCallbackQuery().getData().equals("fb-two")) {
                createDocument(backMessage, collection, update.getCallbackQuery().getData(), update, mongoClient,
                        backText);
            } else if (update.getCallbackQuery().getData().equals("pdf")) {
                createDocument(backMessage, collection, update.getCallbackQuery().getData(), update, mongoClient,
                        backText);
            } else if (update.getCallbackQuery().getData().equals("audio")) {
                createAudio(backMessage, collection, update.getCallbackQuery().getData(), update, mongoClient,
                        backText);
            } else if (update.getCallbackQuery().getData().equals("shops")) {
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
                urlShops("Ridero", button1, book, row1);
                urlShops("ЛитРес", button2, book, row2);
                urlShops("Wildberries", button3, book, row2);
                urlShops("OZON", button4, book, row2);
                urlShops("AliExpress", button5, book, row3);
                urlShops("Amazon", button6, book, row3);
                returnButton.setText("Вернуться");
                returnButton.setCallbackData("return");
                row4.add(returnButton);
                rowList.add(row1);
                rowList.add(row2);
                rowList.add(row3);
                rowList.add(row4);
                inlineKeyboard.setKeyboard(rowList);
                keyboard.setReplyMarkup(inlineKeyboard);
                try {
                    execute(keyboard);
                    createLog(update, mongoClient, "*Отобразил клавиатуру " + backText + "*", "Bot ",
                            true);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (backText.equals("return")) {
                EditMessageReplyMarkup backKeyboard = new EditMessageReplyMarkup();
                backKeyboard.setChatId(backMessage.getChatId().toString());
                backKeyboard.setMessageId(Integer.valueOf(backMessage.getMessageId().toString()));
                InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
                createFirstKeyboard(update, inlineKeyboard, collection);
                backKeyboard.setReplyMarkup(inlineKeyboard);
                try {
                    execute(backKeyboard);
                    createLog(update, mongoClient, "*Вернул старую клавиатуру*", "Bot ", true);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void urlShops(String text, InlineKeyboardButton button, Document book, List<InlineKeyboardButton> row) {
        button.setText(text);
        button.setUrl(book.getEmbedded(Arrays.asList("Shops", text), String.class));
        row.add(button);
    }

    private void createMessage(Message message, String text, Update update, MongoClient mongoClient) {
        textLog = text.replaceAll("\\\\", "");
        SendMessage createMessage = new SendMessage();
        createMessage.setChatId(message.getChatId().toString());
        createMessage.setText(text);
        createMessage.enableMarkdownV2(true);
        if (text.equals("Давайте вместе разберемся, чем я могу помочь"))
            createKeyboard(createMessage, update, mongoClient);
        try {
            execute(createMessage);
            createLog(update, mongoClient, textLog, "Bot ", false);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void createFewCovers(Message message, MongoCollection<Document> collection, Update update,
                                 MongoClient mongoClient) {
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
            createLog(update, mongoClient, "*Показал несколько обложек*", "Bot ", false);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void createCover(Update update, Message message, MongoCollection<Document> collection,
                             MongoClient mongoClient) {
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
            createLog(update, mongoClient, "*Показал обложку с кнопками*", "Bot ", false);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void createDocument(Message message, MongoCollection<Document> collection, String whichButton,
                                Update update, MongoClient mongoClient, String backText) {
        SendDocument document = new SendDocument();
        document.setChatId(message.getChatId().toString());
        Document doc = collection.find().skip(showBook).first();
        assert doc != null;
        document.setDocument(new InputFile(doc.getString(whichButton)));
        try {
            execute(document);
            createLog(update, mongoClient, "*Прислал документ " + backText + "*", "Bot ", true);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void createAudio(Message message, MongoCollection<Document> collection, String whichButton,
                             Update update, MongoClient mongoClient, String backText) {
        SendAudio audio = new SendAudio();
        audio.setChatId(message.getChatId().toString());
        Document doc = collection.find().skip(showBook).first();
        assert doc != null;
        audio.setAudio(new InputFile(doc.getString(whichButton)));
        try {
            execute(audio);
            createLog(update, mongoClient, "*Прислал аудио " + backText + "*", "Bot ", true);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void createFirstKeyboard(Update update, InlineKeyboardMarkup inlineKeyboard,
                                    MongoCollection<Document> collection) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        InlineKeyboardButton ShopsButton = new InlineKeyboardButton();
        InlineKeyboardButton NextButton = new InlineKeyboardButton();
        InlineKeyboardButton ExcerptButton = new InlineKeyboardButton();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2_3 = new ArrayList<>();
        List<InlineKeyboardButton> row3_4 = new ArrayList<>();
        ShopsButton.setText("Книга в магазинах");
        ShopsButton.setCallbackData("shops");
        NextButton.setText("Следующая книга");
        NextButton.setCallbackData("next");
        ExcerptButton.setText("Отрывок из книги");
        ExcerptButton.setCallbackData("excerpt");
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
            PreviousButton.setCallbackData("previous");
            row2_3.add(PreviousButton);
        }
        row2_3.add(NextButton);
        row3_4.add(ExcerptButton);
        rowList.add(row2_3);
        rowList.add(row3_4);
        inlineKeyboard.setKeyboard(rowList);
    }

    private void createKeyboard(SendMessage createMessage, Update update, MongoClient mongoClient) {
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
        createLog(update, mongoClient, "*Клавиатуру нарисовал*", "Bot ", false);
    }

    public void createLog(Update update, MongoClient mongoClient, String textLog, String who, Boolean keyboard) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        DateFormat dateFormatLog = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        /* tomorrow
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        date = calendar.getTime();
        */
        MongoDatabase databaseLog = mongoClient.getDatabase("Log");
        MongoCollection<Document> collectionLog = databaseLog.getCollection("Log");
        if (keyboard) {
            Id = update.getCallbackQuery().getFrom().getId().toString();
            Info = "InfoKeyboard";
            Answer = update.getCallbackQuery().getMessage().toString();
        } else {
            Id = update.getMessage().getFrom().getId().toString();
            Info = "Info";
            Answer = update.getMessage().toString();
        }
        try {
            collectionLog.insertOne(
                    new Document().append("_id", Id).append(Info, Answer).append(dateFormat.format(date),
                            dateFormatLog.format(date) + " " + who + ": " + textLog + "\n"));
        } catch (MongoException me) {
            Document doc = collectionLog.find(Filters.eq("_id", Id)).first();
            assert doc != null;
            Document query = new Document().append("_id", Id);
            if (doc.getString(dateFormat.format(date)) == null) Script = "";
            else Script = doc.getString(dateFormat.format(date));
            Bson updates = Updates.combine(Updates.set(dateFormat.format(date), Script + dateFormatLog.format(date) +
                    " " + who + ": " + textLog + "\n"));
            UpdateOptions options = new UpdateOptions().upsert(true);
            collectionLog.updateOne(query, updates, options);
        }
    }
}