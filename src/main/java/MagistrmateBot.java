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
    public static final String PZV_COVER = "AgACAgIAAxkBAAIBx2KTdP4CNbqTZfv7Hm7TqGAugkdSAAKIvjEbUaqZSPwL-Up482owAQADAgADeQADJAQ";
    public static final String PZV_NAME = "Параллельно задавая вопрос";
    public static final String PZV_DESC = """
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
            Дружба между парнем и девушкой существует и поддерживается государством, давая им невероятные способности\\.""";
    public static final String PZV_RIDERO = "https://ridero.ru/books/parallelno_zadavaya_vopros/";
    public static final String PZV_LITRES = "https://www.litres.ru/daniil-apasov/parallelno-zadavaya-vopros-pod-pokrovom-edinstva-korrektirov/";
    public static final String PZV_WILDBERRIES = "https://www.wildberries.ru/catalog/36734671/detail.aspx?targetUrl=SN";
    public static final String PZV_OZON = "https://www.ozon.ru/product/parallelno-zadavaya-vopros-168137107/?sh=qwZ99MK_wQ";
    public static final String PZV_ALIEXPRESS = "https://aliexpress.ru/item/1005002349414876.html?gatewayAdapt=glo2rus&sku_id=12000020229783418";
    public static final String PZV_AMAZON = "https://www.amazon.com/dp/B084Q3G56J";
    public static final String POD_COVER = "AgACAgIAAxkBAAICIWKV6rrWZCfAVHeZRb600fEdhUUtAALFuTEbgvKxSGOQfTzAxJOgAQADAgADeQADJAQ";
    public static final String POD_NAME = "Под покровом единства";
    public static final String POD_DESC = "Никогда не задумывались, что друзья — это ваша слабость? Иногда вы" +
            "рискуете всем ради них\\. Государство и учебные заведения об этом не расскажут, кроме одного вуза со" +
            "спецпрограммой, который сделает из каждого студента личность\\. Где все связи будут запрещены\\." +
            "Самостоятельность во главе всего\\. Все ли смогут пройти такую школу жизни?";
    public static final String KORR_COVER = "AgACAgIAAxkBAAICHWKV6h4KSQAB0Cw-9YYzNmKqQYlBbAACwrkxG4LysUioRIR5O_3J4wEAAwIAA3kAAyQE";
    public static final String KORR_NAME = "Корректировка";
    public static final String KORR_DESC = "Каждый из нас задумывается о такой штуке, как судьба\\. Что привело нас к" +
            "сегодняшнему дню? Друзья, знакомые, парни и девушки\\. Все ли эти отношения образовались сами собой, или," +
            "быть может, кто\\-то из твоих близких друзей всего лишь хорошо разыграл все карты несколько лет назад?";
    public static final String LUNN_COVER = "AgACAgIAAxkBAAICH2KV6lrfNbsYfnqVDQwV1uhexKUGAALDuTEbgvKxSBEP75XimlVwAQADAgADeQADJAQ";
    public static final String LUNN_NAME = "Лунные тени";
    private static final String LUNN_DESC = "Общежитие при университете — особый период в жизни каждого человека\\." +
            "Здесь происходит в основном много приятных событий, запоминающихся на всю жизнь\\. Однако судьбы трёх" +
            "друзей это место однажды навсегда изменит\\. Им предстоит череда ночей, когда редко все оставались к " +
            "утру живы\\. Что же делать в ситуации, когда врагом является твой спящий друг?";
    public static final String ZVEZDA_COVER = "AgACAgIAAxkBAAICG2KV6b_hGJ0jk_yHtzUWmfXrs0K-AALBuTEbgvKxSE55vwxxNxcxAQADAgADeQADJAQ";
    public static final String ZVEZDA_NAME = "Звезда";
    private static final String ZVEZDA_DESC = "Детские мечты не всегда сбываются так, как вы того хотели\\. " +
            "Кирили, обычный парень, которому предстоит испытать невероятные впечатления, когда он всё же сможет " +
            "прикоснуться к своей мечте\\. Сможет ли он совладать с человеческими пороками и спасти Землю от уничтожения?";
    public static final String PON_COVER = "AgACAgIAAxkBAAICI2KV6u5ALDMcSPP4WPsvdr5iBJ1hAALGuTEbgvKxSGlGhmGbA1qtAQADAgADeQADJAQ";
    public static final String PON_NAME = "Понимания ноль";
    private static final String PON_DESC = "Привет\\. Я тут рассказал неординарную историю нашего путешествия и " +
            "немного о нашем мире\\. Представляете, у нас дружба между парнем и девушкой возможна\\. Причем на " +
            "законодательном уровне\\. Мы начинаем обладать особыми силами, но это уже подробнее внутри\\. Возможно, " +
            "ваша жизненная ситуация похожа на нашу, и вы являетесь таким другом противоположному себе полу, а?";
    String BOOK_COVER;
    String BOOK_NAME;
    String BOOK_DESC;

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
            if (message.getText().equals("/start")) {
                createMessage(message, "Добро пожаловать " + message.getFrom().getFirstName() + "\\!\n" +
                        "Мы можем перейти сразу к книгам или пообщаться\\. Я пока в процессе познания вашего мира," +
                        " поэтому пишите и если не пойму, то выдам вам подсказки\\.");
            } else if (update.getMessage().getText().toLowerCase(Locale.ROOT).contains("привет")) {
                createMessage(message, "Дороу");
            } else if (update.getMessage().getText().toLowerCase(Locale.ROOT).contains("книг") ||
                    update.getMessage().getText().toLowerCase(Locale.ROOT).contains("книж")) {
                createMediaGroup(message);
                createPhoto(update, message);
            } else
                createMessage(message, "Давайте вместе разберемся, чем я могу помочь");
        } else if (update.hasCallbackQuery()) {
            Message backMessage = update.getCallbackQuery().getMessage();
            if (update.getCallbackQuery().getData().equals("NextBook") ||
                    update.getCallbackQuery().getData().equals("PreviousBook")) {
                if (update.getCallbackQuery().getData().equals("NextBook")) {
                    if (backMessage.getCaption().contains(PZV_NAME)) {
                        whichBook(POD_COVER, POD_NAME, POD_DESC);
                    } else if (backMessage.getCaption().contains(POD_NAME)) {
                        whichBook(KORR_COVER, KORR_NAME, KORR_DESC);
                    } else if (backMessage.getCaption().contains(KORR_NAME)) {
                        whichBook(LUNN_COVER, LUNN_NAME, LUNN_DESC);
                    } else if (backMessage.getCaption().contains(LUNN_NAME)) {
                        whichBook(ZVEZDA_COVER, ZVEZDA_NAME, ZVEZDA_DESC);
                    } else if (backMessage.getCaption().contains(ZVEZDA_NAME)) {
                        whichBook(PON_COVER, PON_NAME, PON_DESC);
                    } else if (backMessage.getCaption().contains(PON_NAME)) {
                        whichBook(PZV_COVER, PZV_NAME, PZV_DESC);
                    }
                } else if (update.getCallbackQuery().getData().equals("PreviousBook")) {
                    if (backMessage.getCaption().contains(PZV_NAME)) {
                        whichBook(PON_COVER, PON_NAME, PON_DESC);
                    } else if (backMessage.getCaption().contains(POD_NAME)) {
                        whichBook(PZV_COVER, PZV_NAME, PZV_DESC);
                    } else if (backMessage.getCaption().contains(KORR_NAME)) {
                        whichBook(POD_COVER, POD_NAME, POD_DESC);
                    } else if (backMessage.getCaption().contains(LUNN_NAME)) {
                        whichBook(KORR_COVER, KORR_NAME, KORR_DESC);
                    } else if (backMessage.getCaption().contains(ZVEZDA_NAME)) {
                        whichBook(LUNN_COVER, LUNN_NAME, LUNN_DESC);
                    } else if (backMessage.getCaption().contains(PON_NAME)) {
                        whichBook(ZVEZDA_COVER, ZVEZDA_NAME, ZVEZDA_DESC);
                    }
                }
                InputMedia cover = new InputMediaPhoto();
                cover.setMedia(BOOK_COVER);
                cover.setCaption("*" + BOOK_NAME + "*\n" + BOOK_DESC);
                cover.setParseMode(ParseMode.MARKDOWNV2);
                EditMessageMedia replacePhoto = new EditMessageMedia();
                replacePhoto.setMedia(cover);
                replacePhoto.setChatId(backMessage.getChatId().toString());
                replacePhoto.setMessageId(Integer.valueOf(backMessage.getMessageId().toString()));
                InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
                createFirstKeyboard(update, inlineKeyboard);
                replacePhoto.setReplyMarkup(inlineKeyboard);
                try {
                    execute(replacePhoto);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
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
                createFirstKeyboard(update, inlineKeyboard);
                backKeyboard.setReplyMarkup(inlineKeyboard);
                try {
                    execute(backKeyboard);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void whichBook(String cover, String name, String describe) {
        BOOK_COVER = cover;
        BOOK_NAME = name;
        BOOK_DESC = describe;
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

    private void createMediaGroup(Message message) {
        List<InputMedia> media = new ArrayList<>();
        InputMedia photo1 = new InputMediaPhoto();
        photo1.setParseMode(ParseMode.MARKDOWNV2);
        photo1.setMedia(PZV_COVER);
        photo1.setCaption("*" + PZV_NAME + "*\n" + PZV_DESC);
        InputMedia photo2 = new InputMediaPhoto();
        photo2.setParseMode(ParseMode.MARKDOWNV2);
        photo2.setMedia(KORR_COVER);
        photo2.setCaption("*" + KORR_NAME + "*\n" + KORR_DESC);
        InputMedia photo3 = new InputMediaPhoto();
        photo3.setParseMode(ParseMode.MARKDOWNV2);
        photo3.setMedia(LUNN_COVER);
        photo3.setCaption("*" + LUNN_NAME + "*\n" + LUNN_DESC);
        InputMedia photo4 = new InputMediaPhoto();
        photo4.setParseMode(ParseMode.MARKDOWNV2);
        photo4.setMedia(ZVEZDA_COVER);
        photo4.setCaption("*" + ZVEZDA_NAME + "*\n" + ZVEZDA_DESC);
        InputMedia photo5 = new InputMediaPhoto();
        photo5.setParseMode(ParseMode.MARKDOWNV2);
        photo5.setMedia(PON_COVER);
        photo5.setCaption("*" + PON_NAME + "*\n" + PON_DESC);
        media.add(photo1);
        media.add(photo2);
        media.add(photo3);
        media.add(photo4);
        media.add(photo5);
        SendMediaGroup mediaGroup = new SendMediaGroup();
        mediaGroup.setChatId(message.getChatId().toString());
        mediaGroup.setMedias(media);
        try {
            execute(mediaGroup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void createPhoto(Update update, Message message) {
        SendPhoto createPhoto = new SendPhoto();
        createPhoto.setParseMode(ParseMode.MARKDOWNV2);
        createPhoto.setChatId(message.getChatId().toString());
        createPhoto.setPhoto(new InputFile(PZV_COVER));
        createPhoto.setCaption("*" + PZV_NAME + "*\n" + PZV_DESC);
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        createFirstKeyboard(update, inlineKeyboard);
        createPhoto.setReplyMarkup(inlineKeyboard);
        try {
            execute(createPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void createFirstKeyboard(Update update, InlineKeyboardMarkup inlineKeyboard) {
        InlineKeyboardButton ShopsBookButton = new InlineKeyboardButton();
        ShopsBookButton.setText("Книга в магазинах");
        ShopsBookButton.setCallbackData("ShopsBook");
        InlineKeyboardButton NextBookButton = new InlineKeyboardButton();
        NextBookButton.setText("Следующая книга");
        NextBookButton.setCallbackData("NextBook");
        InlineKeyboardButton ExcerptBookButton = new InlineKeyboardButton();
        ExcerptBookButton.setText("Отрывок из книги");
        ExcerptBookButton.setCallbackData("ExcerptBook");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(ShopsBookButton);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        if (update.getCallbackQuery() != null) {
            InlineKeyboardButton PreviousBookButton = new InlineKeyboardButton();
            PreviousBookButton.setText("Предыдущая книга");
            PreviousBookButton.setCallbackData("PreviousBook");
            row2.add(PreviousBookButton);
        }
        row2.add(NextBookButton);
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(ExcerptBookButton);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);
        rowList.add(row2);
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
    /* hg   private void editPhoto(Message message, EditMessageMedia replacePhoto) {
            replacePhoto.setChatId(message.getChatId().toString());
            if (message.getReplyMarkup().getKeyboard().get(0).get(0).getCallbackData().equals("NextBook")) {
                InputMedia photoE = new InputMediaPhoto();
                photoE.setMedia(POD_COVER);

            } else {

            }
            createInlineKeyboard(message, editPhoto);
        }*/
/*        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
                long chat_id = update.getMessage().getChatId();
                List<PhotoSize> photos = update.getMessage().getPhoto();
                String f_id = Objects.requireNonNull(photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                        .orElse(null)).getFileId();
                String caption = "file_id: " + f_id;
                SendPhoto msg = new SendPhoto();
                msg.setPhoto(new InputFile(f_id));
                msg.setChatId(String.valueOf(chat_id));
                msg.setCaption(caption);
                try {
                    execute(msg);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
        }
        */
/*
               EditMessageText newMessage = new EditMessageText();
               newMessage.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
               newMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
               newMessage.setText("Updated message text");
               */
/*
https://core.telegram.org/bots/api#markdownv2-style
createMessage(message, "*Добро* _пожаловать_ " + message.getFrom().getFirstName() + "\\!\n" +
        "Мы можем ~перейти~ сразу к ||книгам|| или пообщаться\\. Я пока в [сайтик](http://www.example.com/) процессе познания вашего мира\\," +
        " поэтому ```некоторые``` __ответы__ от [Dante](tg://user?id=411435416) меня будут сюрпризом\\. Если ты их не любишь\\, то просто" +
        " воспользуйся `моей` клавиатурой внизу");
        */
            /*few ArrayList<String> array1 = new ArrayList<String>();
            array1.add(PZV_COVER);
            array1.add(POD_COVER);
            array1.add(KORR_COVER);
            array1.add(LUNN_COVER);
            array1.add(ZVEZDA_COVER);
            array1.add(PON_COVER);
            for (String i:array1) {
                System.out.println(i);
            }*/