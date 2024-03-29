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

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.mongodb.client.model.Filters.eq;

public class MagistrmateBot extends TelegramLongPollingBot {
    Integer showBook;
    Boolean nextBookUse = false;
    String textLog;
    String id;
    String answer;
    String script;
    String userIdTalkSupport = "";
    String userIdTalkSupportWait = "";
    String textHistory;
    String name;
    String username;
    Boolean notification = true;
    String notificationId = "";
    String chatId;
    String text;
    MongoClient mongoClient = MongoClients.create(BotConfig.DB_TOKEN);
    MongoDatabase database = mongoClient.getDatabase("MagistrmateDatabase");
    MongoCollection<Document> collection = database.getCollection("MagistrmateCollection");
    MongoCollection<Document> collectionLog = database.getCollection("Log");
    Boolean waitId = false;
    Boolean waitText = false;
    String whoId;
    String parseMode;

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
        Message message = update.getMessage();
        if (update.hasMessage()) {
            chatId = message.getChatId().toString();
            username = message.getFrom().getUserName();
            name = message.getFrom().getFirstName();
            text = message.getText();
            if (chatId.equals(BotConfig.USER_SUPPORT)) {
                if (!waitText) createLog(update, text, "User", false);
                if (!userIdTalkSupport.equals("")) {
                    createMessage(text, update, userIdTalkSupport);
                    if (text.contains("До свидания")) {
                        if (userIdTalkSupportWait.equals("") || userIdTalkSupport.equals(userIdTalkSupportWait)) {
                            userIdTalkSupport = "";
                        } else {
                            userIdTalkSupport = userIdTalkSupportWait;
                            createMessage("Оператор сейчас вам ответит", update, userIdTalkSupportWait);
                            createHistory(userIdTalkSupport);
                        }
                    }
                } else {
                    if (text.contains("Нарисуй клаву")) {
                        createKeyboardSupport("Окей, нарисовал", chatId);
                    } else if (text.toLowerCase().contains("забей")) {
                        waitId = false;
                        waitText = false;
                        createMessage("Забил", update, BotConfig.USER_SUPPORT);
                    } else if (text.equals("список юзеров")) {
                        List<Document> usernames = collectionLog.find().into(new ArrayList<>());
                        StringBuilder textAll = new StringBuilder();
                        //int i = 1;
                        for (Document username : usernames) {
                            textAll.append(username.getString("_id")).append("\n");
                            //i++;
                        }
                        SendMessage createMessage = SendMessage.builder()
                                .chatId(chatId)
                                .text(String.valueOf(textAll))
                                .build();
                        try {
                            execute(createMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else if (text.equals("отправь сообщение")) {
                        createMessage("Кому отправить?", update, chatId);
                        waitId = true;
                    } else if (waitId) {
                        whoId = text;
                        createMessage("Что отправить?", update, BotConfig.USER_SUPPORT);
                        waitId = false;
                        waitText = true;
                    } else if (waitText) {
                        createMessage(text, update, whoId);
                        waitText = false;
                        createLog(update, text, "User", false);
                        createMessage("Отправил", update, BotConfig.USER_SUPPORT);
                    } else createMessage("Ты втираешь мне какую\\-то дичь", update, BotConfig.USER_SUPPORT);
                }
            } else if (chatId.equals(userIdTalkSupport))
                createMessage(text, update, BotConfig.USER_SUPPORT);
            else createTalk(message, update);
            if (!chatId.equals(BotConfig.USER_SUPPORT) && userIdTalkSupport.equals("") && !chatId.equals(BotConfig.USER_ME)) {
                if (notification && !chatId.equals(notificationId)) {
                    createMessage("Со мной общается @" + username + "\\(" + name + "\\)", update, BotConfig.USER_SUPPORT);
                    notification = false;
                    notificationId = "";
                } else {
                    notification = true;
                    notificationId = chatId;
                }
            }
        }
        if (update.hasCallbackQuery()) {
            Message backMessage = update.getCallbackQuery().getMessage();
            String backText = update.getCallbackQuery().getData();
            chatId = backMessage.getChatId().toString();
            Integer messageId = backMessage.getMessageId();
            createLog(update, "*Нажал на кнопку " + backText + "*", "User", true);
            Document doc = collectionLog.find(eq("_id", id)).first();
            assert doc != null;
            if (backText.equals("next") || backText.equals("previous") || backText.matches(".*\\d+.*")) {
                if (backText.equals("next")) {
                    if (doc.getInteger("NumberBook") + 1 == collection.countDocuments()) showBook = 0;
                    else showBook = doc.getInteger("NumberBook") + 1;
                } else if (backText.equals("previous")) {
                    if (doc.getInteger("NumberBook") == 0 && nextBookUse) showBook = 5;
                    else showBook = doc.getInteger("NumberBook") - 1;
                } else if (backText.matches(".*\\d+.*")) showBook = Integer.parseInt(backText) - 1;
                nextBookUse = true;
                changeNumberBook(showBook);
                Document book = collection.find().skip(showBook).first();
                assert book != null;
                InputMedia photo = InputMediaPhoto.builder()
                        .media(book.getString("cover"))
                        .caption("*" + book.getString("name") + "*\n" + book.getString("description"))
                        .parseMode("MarkdownV2").build();
                EditMessageMedia replacePhoto = EditMessageMedia.builder()
                        .media(photo).chatId(chatId)
                        .messageId(messageId).build();
                InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
                createFirstKeyboard(update, inlineKeyboard);
                replacePhoto.setReplyMarkup(inlineKeyboard);
                try {
                    execute(replacePhoto);
                    createLog(update, "*Перелистнул книгу*", "Bot ", true);
                } catch (TelegramApiException e) {
                    createMessage("Смена обложки\n" + e, update, BotConfig.USER_SUPPORT);
                }
            } else if (backText.equals("excerpt")) {
                Document book = collection.find().skip(doc.getInteger("NumberBook")).first();
                assert book != null;
                EditMessageReplyMarkup keyboard = EditMessageReplyMarkup.builder()
                        .chatId(chatId)
                        .messageId(messageId).build();
                InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                List<InlineKeyboardButton> row2 = new ArrayList<>();
                List<InlineKeyboardButton> row3 = new ArrayList<>();
                List<InlineKeyboardButton> row4 = new ArrayList<>();
                InlineKeyboardButton button1 = InlineKeyboardButton.builder()
                        .text("🌍 Online")
                        .callbackData("online")
                        .url(book.getString("excerpt")).build();
                InlineKeyboardButton button2 = InlineKeyboardButton.builder()
                        .text("📘 EPUB")
                        .callbackData("epub").build();
                InlineKeyboardButton button3 = InlineKeyboardButton.builder()
                        .text("📙 FB2")
                        .callbackData("fb-two").build();
                InlineKeyboardButton button4 = InlineKeyboardButton.builder()
                        .text("📕 PDF")
                        .callbackData("pdf").build();
                InlineKeyboardButton button5 = InlineKeyboardButton.builder()
                        .text("🎧 Аудио")
                        .callbackData("audio").build();
                InlineKeyboardButton returnButton = InlineKeyboardButton.builder()
                        .text("↩ Вернуться")
                        .callbackData("return").build();
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
                    createLog(update, "*Поменял клавиатуру на отрывки*", "Bot ", true);
                } catch (TelegramApiException e) {
                    createMessage("Показал отрывки\n" + e, update, BotConfig.USER_SUPPORT);
                }
            } else if (backText.equals("epub")) {
                createDocument(backMessage, backText, update, doc);
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
                }*/
            } else if (update.getCallbackQuery().getData().equals("fb-two")) {
                createDocument(backMessage, backText, update, doc);
            } else if (update.getCallbackQuery().getData().equals("pdf")) {
                createDocument(backMessage, backText, update, doc);
            } else if (update.getCallbackQuery().getData().equals("audio")) {
                createAudio(backMessage, backText, update, doc);
            } else if (update.getCallbackQuery().getData().equals("shops")) {
                Document book = collection.find().skip(doc.getInteger("NumberBook")).first();
                assert book != null;
                EditMessageReplyMarkup keyboard = EditMessageReplyMarkup.builder()
                        .chatId(chatId)
                        .messageId(messageId).build();
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
                urlShops("Ridero", "🟠", button1, book, row1);
                urlShops("ЛитРес", "⚪", button2, book, row2);
                urlShops("Wildberries", "🟣", button3, book, row2);
                urlShops("OZON", "🔵", button4, book, row2);
                urlShops("AliExpress", "🔴", button5, book, row3);
                urlShops("Amazon", "🟡", button6, book, row3);
                returnButton.setText("↩ Вернуться");
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
                    createLog(update, "*Отобразил клавиатуру " + backText + "*", "Bot ", true);
                } catch (TelegramApiException e) {
                    createMessage("Показал магазины\n" + e, update, BotConfig.USER_SUPPORT);
                }
            } else if (backText.equals("return")) {
                EditMessageReplyMarkup backKeyboard = new EditMessageReplyMarkup();
                backKeyboard.setChatId(backMessage.getChatId().toString());
                backKeyboard.setMessageId(backMessage.getMessageId());
                InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
                createFirstKeyboard(update, inlineKeyboard);
                backKeyboard.setReplyMarkup(inlineKeyboard);
                try {
                    execute(backKeyboard);
                    createLog(update, "*Вернул старую клавиатуру*", "Bot ", true);
                } catch (TelegramApiException e) {
                    createMessage("Нажали возврат\n" + e, update, BotConfig.USER_SUPPORT);
                }
            }
        }
    }

    public void urlShops(String shop, String emoji, InlineKeyboardButton button, Document book, List<InlineKeyboardButton> row) {
        button.setText(emoji + " " + shop);
        button.setUrl(book.getEmbedded(Arrays.asList("Shops", shop), String.class));
        row.add(button);
    }

    private void createMessage(String text, Update update, String sentId) {
        if (waitText) parseMode = "";
        else parseMode = "MarkdownV2";
        SendMessage createMessage = SendMessage.builder()
                .chatId(sentId)
                .text(text)
                .parseMode(parseMode).build();
        if (text.equals("Давайте вместе разберемся, чем я могу помочь🤔"))
            createKeyboard(createMessage, update);
        try {
            execute(createMessage);
            if (!text.contains("Со мной общается @") && !text.equals("Ало, там очередь уже!")) {
                textLog = text.replaceAll("\\\\", "");
                createLog(update, textLog, "Bot ", false);
            }
        } catch (TelegramApiException e) {
            createLog(update, e.toString(), id, false);
            SendMessage errorMessage = SendMessage.builder()
                    .chatId(BotConfig.USER_SUPPORT)
                    .text(e.toString()).build();
            try {
                execute(errorMessage);
            } catch (TelegramApiException y) {
                y.printStackTrace();
            }
        }
    }

    private void createFewCovers(Message message, Update update) {
        List<InputMedia> media = new ArrayList<>();
        List<Document> books = collection.find().skip(1).into(new ArrayList<>());
        for (Document book : books) {
            InputMedia photo = InputMediaPhoto.builder()
                    .parseMode("MarkdownV2")
                    .media(book.getString("cover"))
                    .caption("*" + book.getString("name") + "*\n" + book.getString("description")).build();
            media.add(photo);
        }
        SendMediaGroup mediaGroup = SendMediaGroup.builder()
                .chatId(message.getChatId().toString())
                .medias(media).build();
        try {
            execute(mediaGroup);
            createLog(update, "*Показал несколько обложек*", "Bot ", false);
        } catch (TelegramApiException e) {
            createMessage("Показал несколько обложек\n" + e, update, BotConfig.USER_SUPPORT);
        }
    }

    private void createCover(Update update, String chatId) {
        Document doc = collection.find().limit(1).first();
        assert doc != null;
        SendPhoto photo = SendPhoto.builder()
                .parseMode("MarkdownV2")
                .chatId(chatId)
                .photo(new InputFile(doc.getString("cover")))
                .caption("*" + doc.getString("name") + "*\n" + doc.getString("description")).build();
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        createFirstKeyboard(update, inlineKeyboard);
        photo.setReplyMarkup(inlineKeyboard);
        try {
            execute(photo);
            createLog(update, "*Показал обложку с кнопками*", "Bot ", false);
        } catch (TelegramApiException e) {
            createMessage("Обложка с кнопками\n" + e, update, BotConfig.USER_SUPPORT);
        }
    }

    private void createDocument(Message message, String backText, Update update, Document doc) {
        SendDocument document = new SendDocument();
        document.setChatId(message.getChatId().toString());
        Document book = collection.find().skip(doc.getInteger("NumberBook")).first();
        assert book != null;
        document.setDocument(new InputFile(book.getString(backText)));
        try {
            execute(document);
            createLog(update, "*Прислал " + backText + "*", "Bot ", true);
        } catch (TelegramApiException e) {
            createMessage("Документ прислал\n" + e, update, BotConfig.USER_SUPPORT);
        }
    }

    private void createAudio(Message message, String backText, Update update, Document doc) {
        SendAudio audio = new SendAudio();
        audio.setChatId(message.getChatId().toString());
        Document book = collection.find().skip(doc.getInteger("NumberBook")).first();
        assert book != null;
        audio.setAudio(new InputFile(book.getString(backText)));
        try {
            execute(audio);
            createLog(update, "*Прислал " + backText + "*", "Bot ", true);
        } catch (TelegramApiException e) {
            createMessage("Аудио прислал\n" + e, update, BotConfig.USER_SUPPORT);
        }
    }

    public void createFirstKeyboard(Update update, InlineKeyboardMarkup inlineKeyboard) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2_3 = new ArrayList<>();
        List<InlineKeyboardButton> row3_4 = new ArrayList<>();
        InlineKeyboardButton shopsButton = InlineKeyboardButton.builder().text("🛍 Книга в магазинах")
                .callbackData("shops").build();
        InlineKeyboardButton nextButton = InlineKeyboardButton.builder().text("➡ Следующая")
                .callbackData("next").build();
        InlineKeyboardButton excerptButton = InlineKeyboardButton.builder().text("📄 Отрывок из книги")
                .callbackData("excerpt").build();
        row1.add(shopsButton);
        rowList.add(row1);
        if (update.getCallbackQuery() != null && nextBookUse) {
            List<InlineKeyboardButton> row2 = new ArrayList<>();
            for (int i = 1; i <= collection.countDocuments(); i++) {
                InlineKeyboardButton bookButton = new InlineKeyboardButton();
                if (showBook + 1 == i) bookButton.setText("🔹" + i + "🔹");
                else bookButton.setText(String.valueOf(i));
                bookButton.setCallbackData(String.valueOf(i));
                row2.add(bookButton);
            }
            rowList.add(row2);
            InlineKeyboardButton previousButton = InlineKeyboardButton.builder().text("Предыдущая ⬅")
                    .callbackData("previous").build();
            row2_3.add(previousButton);
        }
        row2_3.add(nextButton);
        row3_4.add(excerptButton);
        rowList.add(row2_3);
        rowList.add(row3_4);
        inlineKeyboard.setKeyboard(rowList);
    }

    private void createKeyboard(SendMessage createMessage, Update update) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add("📚 Книги");
        row1.add("🔈 Аудиокниги");
        row2.add("📝 Об авторе");
        row2.add("👤 Позвать оператора");
        keyboard.add(row1);
        keyboard.add(row2);
        ReplyKeyboardMarkup createKeyboard = ReplyKeyboardMarkup.builder()
                .keyboard(keyboard)
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .inputFieldPlaceholder("Пишите, я читаю")
                .selective(true).build();
        createMessage.setReplyMarkup(createKeyboard);
        createLog(update, "*Клавиатуру нарисовал*", "Bot ", false);
    }

    public void createKeyboardSupport(String text, String sentId) {
        SendMessage createMessage = SendMessage.builder()
                .chatId(sentId)
                .text(text)
                .parseMode("MarkdownV2").build();
        List<KeyboardRow> keyboardSupport = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add("список юзеров");
        row1.add("отправь сообщение");
        row2.add("кнопка 3");
        row2.add("кнопка 4");
        keyboardSupport.add(row1);
        keyboardSupport.add(row2);
        ReplyKeyboardMarkup createKeyboard = ReplyKeyboardMarkup.builder()
                .keyboard(keyboardSupport)
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .inputFieldPlaceholder("Пиши давай")
                .selective(true).build();
        createMessage.setReplyMarkup(createKeyboard);
        try {
            execute(createMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void createLog(Update update, String textLog, String who, Boolean keyboard) {
        Instant instant = Instant.now();
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.of("Europe/Moscow"));
        regionDay();
        DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
        String timeString = zdt.format(time);
        if (keyboard) {
            id = update.getCallbackQuery().getFrom().getId().toString();
            answer = update.getCallbackQuery().getMessage().toString();
        } else {
            if (chatId.equals(BotConfig.USER_SUPPORT) && waitText) {
                id = whoId;
                Document doc = collectionLog.find(eq("_id", id)).first();
                assert doc != null;
                answer = doc.getString("Info");
                name = doc.getString("Name");
                username = doc.getString("Username");
            } else {
                id = update.getMessage().getFrom().getId().toString();
                answer = update.getMessage().toString();
                name = update.getMessage().getFrom().getFirstName();
                username = update.getMessage().getFrom().getUserName();
            }
        }
        try {
            collectionLog.insertOne(new Document().append("_id", id).append("Info", answer).append("Name", name)
                    .append("Username", username).append(regionDay(), timeString + " " + who + ": " + textLog + "\n"));
        } catch (MongoException me) {
            Document doc = collectionLog.find(eq("_id", id)).first();
            assert doc != null;
            Document query = new Document().append("_id", id);
            if (doc.getString(regionDay()) == null) script = "";
            else script = doc.getString(regionDay());
            Bson updates = Updates.combine(Updates.set(regionDay(), script + timeString + " " + who + ": " + textLog +
                    "\n"));
            UpdateOptions options = new UpdateOptions().upsert(true);
            collectionLog.updateOne(query, updates, options);
            Bson updatesName = Updates.combine(Updates.set("Name", name));
            collectionLog.updateOne(query, updatesName, options);
            Bson updatesUserName = Updates.combine(Updates.set("Username", username));
            collectionLog.updateOne(query, updatesUserName, options);
        }
    }

    public void createHistory(String whoId) {
        Document doc = collectionLog.find(eq("_id", whoId)).first();
        SendMessage createMessage = new SendMessage();
        createMessage.setChatId(BotConfig.USER_SUPPORT);
        regionDay();
        assert doc != null;
        if (doc.getString(regionDay()).length() > 4096) {
            textHistory = "...\n" + doc.getString(regionDay()).substring(3500);
        } else textHistory = doc.getString(regionDay());
        createMessage.setText(textHistory + "Имя: " + doc.getString("Name") + " Логин: @" + doc.getString("Username"));
        createMessage.enableMarkdownV2(false);
        try {
            execute(createMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void createTalk(Message message, Update update) {
        if (message.hasAudio() || message.hasDocument()) {
            Document query = new Document().append("_id", message.getCaption());
            Bson updates = Updates.combine(Updates.set("audio", message.getAudio().getFileId()));
            UpdateOptions options = new UpdateOptions().upsert(true);
            try {
                collection.updateOne(query, updates, options);
            } catch (MongoException me) {
                createMessage("Подключение к базе\n" + me, update, BotConfig.USER_SUPPORT);
            }
        } else {
            String text = message.getText().toLowerCase(Locale.ROOT);
            createLog(update, text, "User", false);
            if (text.equals("/start")) {
                createMessage("Добро пожаловать " + message.getFrom().getFirstName() + "\\!👋\n" +
                        "Мы можем перейти сразу к книгам или пообщаться\\. Я пока в процессе познания вашего " +
                        "мира, поэтому пишите и если не пойму, то выдам вам подсказки\\.", update, chatId);
            } else if (text.contains("прив") || text.contains("хай") || text.contains("здравствуй")) {
                createMessage("Здравствуйте🤖", update, chatId);
            } else if (text.contains("жанр") || text.contains("про что")) {
                createMessage("Книги в разных жанрах от ужасов👻, мистики👹 и научной фантастики🧬 до современной прозы📓 и фэнтези✨", update, chatId);
            } else if (text.contains("обща") || text.contains("говор") || text.contains("болта")) {
                createMessage("Без проблем👌 Благодаря вам я всё социальней😅 Однако, может начаться паника и я выдам вам кнопки🙃", update, chatId);
            } else if (text.contains("книг") || text.contains("книж") || text.contains("отрывок") ||
                    text.contains("психолог") || text.contains("популярн") || (text.contains("ремарк"))) {
                changeNumberBook(0);
                if (text.contains("ремарк"))
                    createMessage("Классные книги пишет👏 @Magistrmate это про другое🤷", update, chatId);
                if (text.contains("психолог") || text.contains("детское воспитание"))
                    createMessage("Таких книг у нас, к сожалению, нет😔 Зато есть эти⬇", update, chatId);
                if (text.contains("популярн"))
                    createMessage("Точной статистики, к сожалению, нет😞, но я бы вам советовал обратить внимание на сборник произведений😁", update, chatId);
                else createFewCovers(message, update);
                createCover(update, chatId);
            } else if (text.contains("оператор")) {
                if (userIdTalkSupport.equals("")) {
                    createMessage("Сейчас позову, минутку🗣", update, chatId);
                    createHistory(chatId);
                    userIdTalkSupport = chatId;
                } else {
                    createMessage("Оператор уже кому\\-то помогает и обязательно вам ответит позже⏳", update, chatId);
                    createMessage("Ало, там очередь уже\\!", update, BotConfig.USER_SUPPORT);
                    userIdTalkSupportWait = chatId;
                }
            } else if (text.contains("об авторе") || (text.contains("о вас") || text.contains("про автора"))) {
                createMessage("""
                        [Апасов Даниил](tg://user?id=411435416) родился и вырос в провинциальном городке далеко от столицы\\. С 18 лет жил в Москве, получил два высших технических образования и продолжил работать в той же сфере\\. У него есть жена, собака и острое желание писать свои истории для вас\\.✍
                        Контакты: 🟦 [VK](vk.com/magistrmate),📷 [Instagram](instagram.com/magistrmate/),🐦 [Twitter](twitter.com/Magistrmate),🧑📖 [Facebook](facebook.com/magistrmate), ✉ magistrmate@ya\\.ru
                        Бот написан автором книг👾""", update, chatId);
            } else if (text.contains("спасибо")) {
                createMessage("Пожалуйста, обращайтесь😌", update, chatId);
            } else if (text.equals("ок") || text.equals("окей")) {
                createMessage("👌", update, chatId);
                createMessage("Рад помочь😏, " + name, update, chatId);
            } else if (text.contains("умеешь")) {
                createMessage("Я могу поддержать разговор💭, показать где купить книги📚 @Magistrmate, дать " +
                        "прочитать📄 или послушать🎧 отрывки из книг, а также могу связать вас с оператором👤, " +
                        "чтобы он помог с решением любых ваших вопросов🙂", update, chatId);
            } else {
                createMessage("Давайте вместе разберемся, чем я могу помочь🤔", update, chatId);
            }
        }
    }

    public String regionDay() {
        Instant instant = Instant.now();
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.of("Europe/Moscow"));
        DateTimeFormatter date = DateTimeFormatter.ofPattern("dd/MM/yy");
        return zdt.format(date);
        /* tomorrow
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        date = calendar.getTime();*/
    }

    public void changeNumberBook(Integer numberBook) {
        Document query = new Document().append("_id", id);
        Bson updates = Updates.combine(Updates.set("NumberBook", numberBook));
        UpdateOptions options = new UpdateOptions().upsert(true);
        collectionLog.updateOne(query, updates, options);
    }
}
    /*public void createInlineKeyboardSupport(InlineKeyboardMarkup inlineKeyboardSupport) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<Document> usernames = collectionLog.find().into(new ArrayList<>());
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        int i = 1;
        for (Document username : usernames) {
            *//* f if (i == 3) {
                List<InlineKeyboardButton> row2 = new ArrayList<>();
            }*//*
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("@" + username);
            button.setCallbackData(String.valueOf(username));
            row1.add(button);
            i++;
        }
        for (i = 0; i <= 15 ; i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("@" + i);
            button.setCallbackData(String.valueOf(i));
            row1.add(button);
        }
        rowList.add(row1);
        inlineKeyboardSupport.setKeyboard(rowList);
    }*/