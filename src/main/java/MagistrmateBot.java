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
    String InfoLog;

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
                            "Мы можем перейти сразу к книгам или пообщаться\\. Я пока в процессе познания вашего мира,"
                            + " поэтому пишите и если не пойму, то выдам вам подсказки\\.", update, mongoClient);
                } else if (text.contains("прив")) {
                    createMessage(message, "Дороу", update, mongoClient);
                } else if (text.toLowerCase(Locale.ROOT).contains("книг") ||
                        text.toLowerCase(Locale.ROOT).contains("книж")) {
                    createFewCovers(message, collection, update, mongoClient);
                    createCover(update, message, collection, mongoClient);
                } else {
                    createMessage(message, "Давайте вместе разберемся, чем я могу помочь", update, mongoClient);
                }
            }
        } else if (update.hasCallbackQuery()) {
            Message backMessage = update.getCallbackQuery().getMessage();
            String backText = update.getCallbackQuery().getData();
            showBook = nextBook - 1;
            if (backText.equals("next") || backText.equals("previous") || backText.matches(".*\\d+.*")) {
                NextBook = true;
                InfoLog = "следующую книгу*";
                if (backText.equals("previous")) {
                    if (nextBook == 1) nextBook = 5;
                    else nextBook = nextBook - 2;
                    InfoLog = "предыдущую книгу*";
                } else if (backText.matches(".*\\d+.*")) {
                    nextBook = Integer.parseInt(backText) - 1;
                    InfoLog = "кнопку " + backText + "*";
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
                    createLog(update, mongoClient, "Перелистнул книгу", "Bot ", false);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (backText.equals("excerpt")) {
                InfoLog = "Отрывок из книги*";
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
                    createLog(update, mongoClient, "Поменял клавиатуру на магазины", "Bot ", true);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (update.getCallbackQuery().getData().equals("epub")) {
                createDocument(backMessage, collection, update.getCallbackQuery().getData(), update, mongoClient);
            } else if (update.getCallbackQuery().getData().equals("fb-two")) {
                createDocument(backMessage, collection, update.getCallbackQuery().getData(), update, mongoClient);
            } else if (update.getCallbackQuery().getData().equals("pdf")) {
                createDocument(backMessage, collection, update.getCallbackQuery().getData(), update, mongoClient);
            } else if (update.getCallbackQuery().getData().equals("audio")) {
                createAudio(backMessage, collection, update.getCallbackQuery().getData(), update, mongoClient);
            } else if (update.getCallbackQuery().getData().equals("shops")) {
                InfoLog = "Книга в магазинах*";
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
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (backText.equals("return")) {
                InfoLog = "Вернуться*";
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
            //createLog(update, mongoClient, "*нажал на " + InfoLog, "User", true);
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
        if (text.equals("Давайте вместе разберемся, чем я могу помочь")) {
            createKeyboard(createMessage);
        }
        try {
            execute(createMessage);
            createLog(update, mongoClient, textLog, "Bot ", false);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void createFewCovers(Message message, MongoCollection<Document> collection, Update update, MongoClient mongoClient) {
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
            createLog(update, mongoClient, "Показал несколько обложек", "Bot ", false);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void createCover(Update update, Message message, MongoCollection<Document> collection, MongoClient mongoClient) {
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
            createLog(update, mongoClient, "Показал обложку с кнопками", "Bot ", false);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void createDocument(Message message, MongoCollection<Document> collection, String whichButton, Update update, MongoClient mongoClient) {
        createLog(update, mongoClient, "*нажал на " + InfoLog, "User", true);
        SendDocument document = new SendDocument();
        document.setChatId(message.getChatId().toString());
        Document doc = collection.find().skip(showBook).first();
        assert doc != null;
        document.setDocument(new InputFile(doc.getString(whichButton)));
        try {
            execute(document);
            createLog(update, mongoClient, "*Прислал документ " + update.getCallbackQuery().getData(), "Bot ", true);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void createAudio(Message message, MongoCollection<Document> collection, String whichButton, Update update, MongoClient mongoClient) {
        SendAudio audio = new SendAudio();
        audio.setChatId(message.getChatId().toString());
        Document doc = collection.find().skip(showBook).first();
        assert doc != null;
        audio.setAudio(new InputFile(doc.getString(whichButton)));
        try {
            execute(audio);
            createLog(update, mongoClient, "*Прислал аудио " + update.getCallbackQuery().getData(), "Bot ", true);
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

    public void createLog(Update update, MongoClient mongoClient, String textLog, String who, Boolean keyboard) {
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyHHmmssSSS");
        Date date = new Date();
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
            collectionLog.insertOne(new Document()
                    .append("_id", Id)
                    .append(Info, Answer)
                    .append(dateFormat.format(date), textLog));
        } catch (MongoException me) {
            Document query = new Document().append("_id", Id);
            Bson updates = Updates.combine(Updates.set(dateFormat.format(date), who + ": " + textLog));
            UpdateOptions options = new UpdateOptions().upsert(true);
            collectionLog.updateOne(query, updates, options);
        }
    }
}