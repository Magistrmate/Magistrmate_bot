import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
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
    public static final String PZV_PICTURE = "AgACAgIAAxkBAAIBx2KTdP4CNbqTZfv7Hm7TqGAugkdSAAKIvjEbUaqZSPwL-Up482owAQADAgADeQADJAQ";
    public static final String PVZ_NAME = "Параллельно задавая вопрос";
    public static final String PVZ_DISC = """
            Сборник из пяти произведений. Подробные аннотации внутри.
            Под покровом единства.
            Существует вуз, где запрещена дружба, и всех обучают рассчитывать только на себя.
            Корректировка.
            В прошлом случайный спор друзей из компании повлиял на их судьбу в настоящем.
            Лунные тени.
            Хождение во сне жителей общаги и кровавые истории под утро.
            Звезда.
            Сказка о том, как на Землю упала Звезда.
            Понимания ноль.
            Дружба между парнем и девушкой существует и поддерживается государством, давая им невероятные способности.""";
    public static final String POD_PICTURE = "AgACAgIAAxkBAAICIWKV6rrWZCfAVHeZRb600fEdhUUtAALFuTEbgvKxSGOQfTzAxJOgAQADAgADeQADJAQ";
    public static final String POD_NAME = "Под покровом единства";
    public static final String POD_DISC = "Никогда не задумывались, что друзья — это ваша слабость? Иногда вы" +
            "рискуете всем ради них. Государство и учебные заведения об этом не расскажут, кроме одного вуза со" +
            "спецпрограммой, который сделает из каждого студента личность. Где все связи будут запрещены." +
            "Самостоятельность во главе всего. Все ли смогут пройти такую школу жизни?";
    public static final String KORR_PICTURE = "AgACAgIAAxkBAAICHWKV6h4KSQAB0Cw-9YYzNmKqQYlBbAACwrkxG4LysUioRIR5O_3J4wEAAwIAA3kAAyQE";
    public static final String KORR_NAME = "Корректировка";
    public static final String KORR_DISC = "Каждый из нас задумывается о такой штуке, как судьба. Что привело нас к" +
            "сегодняшнему дню? Друзья, знакомые, парни и девушки. Все ли эти отношения образовались сами собой, или," +
            "быть может, кто-то из твоих близких друзей всего лишь хорошо разыграл все карты несколько лет назад?";
    public static final String LUNN_PICTURE = "AgACAgIAAxkBAAICH2KV6lrfNbsYfnqVDQwV1uhexKUGAALDuTEbgvKxSBEP75XimlVwAQADAgADeQADJAQ";
    public static final String LUNN_NAME = "Лунные тени";
    public static final String ZVEZDA_PICTURE = "AgACAgIAAxkBAAICG2KV6b_hGJ0jk_yHtzUWmfXrs0K-AALBuTEbgvKxSE55vwxxNxcxAQADAgADeQADJAQ";
    public static final String ZVEZDA_NAME = "Звезда";
    public static final String PON_PICTURE = "AgACAgIAAxkBAAICI2KV6u5ALDMcSPP4WPsvdr5iBJ1hAALGuTEbgvKxSGlGhmGbA1qtAQADAgADeQADJAQ";
    public static final String PON_NAME = "Понимания ноль";
    String BOOK_PHOTO;
    String BOOK_NAME;
    String BOOK_DISC;

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
        if (update.hasMessage() && message.hasText()) {
            String inputText = message.getText();
            if (inputText.equals("/start")) {
                createMessage(message, "Добро пожаловать " + message.getFrom().getFirstName() + "\\!\n" +
                        "Мы можем перейти сразу к книгам или пообщаться\\. Я пока в процессе познания вашего мира\\," +
                        " поэтому некоторые ответы от меня будут сюрпризом\\. Если ты их не любишь\\, то просто" +
                        " воспользуйся `моей` клавиатурой внизу");
                //createInlineKeyBoard(message);
            } else if (update.getMessage().getText().toLowerCase(Locale.ROOT).contains("привет")) {
                createMessage(message, "Дороу");
            } else if (update.getMessage().getText().toLowerCase(Locale.ROOT).contains("кни")) {
                createMediaGroup(message);
                createPhoto(message);
            } else
                createMessage(message, "Даже не знаю, что на это ответить");
        } else if (update.hasCallbackQuery()) {
            Message backMessage = update.getCallbackQuery().getMessage();
            if (update.getCallbackQuery().getData().equals("NextBook")) {
                if (backMessage.getCaption().contains(PVZ_NAME)) {
                    whichBook(POD_PICTURE, POD_NAME, POD_DISC);
                } else if (backMessage.getCaption().contains(POD_NAME)) {
                    whichBook(KORR_PICTURE, KORR_NAME, KORR_DISC);
                }
/*                } else if (backMessage.getCaption().contains(KORR_NAME)) {
                    BOOK_PHOTO = LUNN_PICTURE;
                    BOOK_CAPTION = LUNN_NAME + "\nОписание";
                } else if (backMessage.getCaption().contains(LUNN_NAME)) {
                    BOOK_PHOTO = ZVEZDA_PICTURE;
                    BOOK_CAPTION = ZVEZDA_NAME + "\nОписание";
                } else if (backMessage.getCaption().contains(ZVEZDA_NAME)) {
                    BOOK_PHOTO = PON_PICTURE;
                    BOOK_CAPTION = PON_NAME + "\nОписание";
                } else if (backMessage.getCaption().contains(PON_NAME)) {
                    BOOK_PHOTO = PZV_PICTURE;
                    BOOK_CAPTION = PVZ_NAME + "\nОписание";
                }
            } else if (update.getCallbackQuery().getData().equals("PreviousBook")) {
                if (backMessage.getCaption().contains(PVZ_NAME)) {
                    BOOK_PHOTO = PON_PICTURE;
                    BOOK_CAPTION = PON_NAME + "\nОписание";
                } else if (backMessage.getCaption().contains(POD_NAME)) {
                    BOOK_PHOTO = PZV_PICTURE;
                    BOOK_CAPTION = PVZ_NAME + "\nОписание";
                } else if (backMessage.getCaption().contains(KORR_NAME)) {
                    BOOK_PHOTO = POD_PICTURE;
                    BOOK_CAPTION = POD_NAME + "\nОписание";
                } else if (backMessage.getCaption().contains(LUNN_NAME)) {
                    BOOK_PHOTO = KORR_PICTURE;
                    BOOK_CAPTION = KORR_NAME + "\nОписание";
                } else if (backMessage.getCaption().contains(ZVEZDA_NAME)) {
                    BOOK_PHOTO = LUNN_PICTURE;
                    BOOK_CAPTION = LUNN_NAME + "\nОписание";
                } else if (backMessage.getCaption().contains(PON_NAME)) {
                    BOOK_PHOTO = ZVEZDA_PICTURE;
                    BOOK_CAPTION = ZVEZDA_NAME + "\nОписание";
                }*/
            }
            InputMedia photo = new InputMediaPhoto();
            photo.setMedia(BOOK_PHOTO);
            photo.setCaption(BOOK_NAME + "\n" + BOOK_DISC);
            EditMessageMedia replacePhoto = new EditMessageMedia();
            replacePhoto.setMedia(photo);
            replacePhoto.setChatId(backMessage.getChatId().toString());
            replacePhoto.setMessageId(Integer.valueOf(backMessage.getMessageId().toString()));
            InlineKeyboardMarkup inlineKeyBoard = new InlineKeyboardMarkup();
            InlineKeyboardButton inlineKeyBoardButton1 = new InlineKeyboardButton();
            inlineKeyBoardButton1.setText("Предыдущая книга");
            inlineKeyBoardButton1.setCallbackData("PreviousBook");
            InlineKeyboardButton inlineKeyBoardButton2 = new InlineKeyboardButton();
            inlineKeyBoardButton2.setText("Следующая книга");
            inlineKeyBoardButton2.setCallbackData("NextBook");
            List<InlineKeyboardButton> row1 = new ArrayList<>();
            row1.add(inlineKeyBoardButton1);
            row1.add(inlineKeyBoardButton2);
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            rowList.add(row1);
            inlineKeyBoard.setKeyboard(rowList);
            replacePhoto.setReplyMarkup(inlineKeyBoard);
            try {
                execute(replacePhoto);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void whichBook(String photo, String name, String describe) {
        BOOK_PHOTO = photo;
        BOOK_NAME = name;
        BOOK_DISC = describe;
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

        createKeyBoard(createMessage);

        try {
            execute(createMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void createKeyBoard(SendMessage createMessage) {
        ReplyKeyboardMarkup createKeyBoard = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Книги");
        row1.add("Аудиокниги");
        keyboard.add(row1);
        KeyboardRow row2 = new KeyboardRow();
        row2.add("Контакты");
        row2.add("Позвать оператора");
        keyboard.add(row2);
        createKeyBoard.setKeyboard(keyboard);
        createKeyBoard.setResizeKeyboard(true);
        createKeyBoard.setOneTimeKeyboard(false);
        createKeyBoard.setInputFieldPlaceholder("Общение");
        createKeyBoard.setSelective(true); //https://core.telegram.org/bots/api#replykeyboardmarkup
        createMessage.setReplyMarkup(createKeyBoard);
    }

    private void createMediaGroup(Message message) {
        List<InputMedia> media = new ArrayList<>();
        InputMedia photo1 = new InputMediaPhoto();
        photo1.setMedia(POD_PICTURE);
        photo1.setCaption("Первая книга");
        InputMedia photo2 = new InputMediaPhoto();
        photo2.setMedia(KORR_PICTURE);
        photo2.setCaption("Первая книга");
        InputMedia photo3 = new InputMediaPhoto();
        photo3.setMedia(LUNN_PICTURE);
        photo3.setCaption("Первая книга");
        InputMedia photo4 = new InputMediaPhoto();
        photo4.setMedia(ZVEZDA_PICTURE);
        photo4.setCaption("Первая книга");
        InputMedia photo5 = new InputMediaPhoto();
        photo5.setMedia(PON_PICTURE);
        photo5.setCaption("Первая книга");
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

    private void createPhoto(Message message) {
        SendPhoto createPhoto = new SendPhoto();
        createPhoto.setChatId(message.getChatId().toString());
        createPhoto.setPhoto(new InputFile(PZV_PICTURE));
        createPhoto.setCaption("Параллельно задавая вопрос");
        createInlineKeyBoard(message, createPhoto);
    }

    private void createInlineKeyBoard(Message message, SendPhoto createPhoto) {
        InlineKeyboardMarkup inlineKeyBoard = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyBoardButton = new InlineKeyboardButton();
        inlineKeyBoardButton.setText("Следующая книга");
        inlineKeyBoardButton.setCallbackData("NextBook");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(inlineKeyBoardButton);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);
        inlineKeyBoard.setKeyboard(rowList);
        createPhoto.setChatId(message.getChatId().toString());
        createPhoto.setReplyMarkup(inlineKeyBoard);
        try {
            execute(createPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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
                photoE.setMedia(POD_PICTURE);

            } else {

            }
            createInlineKeyBoard(message, editPhoto);
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
createMessage(message, "*Добро* _пожаловать_ " + message.getFrom().getFirstName() + "\\!\n" +
        "Мы можем ~перейти~ сразу к ||книгам|| или пообщаться\\. Я пока в [сайтик](http://www.example.com/) процессе познания вашего мира\\," +
        " поэтому ```некоторые``` __ответы__ от [Dante](tg://user?id=411435416) меня будут сюрпризом\\. Если ты их не любишь\\, то просто" +
        " воспользуйся `моей` клавиатурой внизу");
        */
            /*few ArrayList<String> array1 = new ArrayList<String>();
            array1.add(PZV_PICTURE);
            array1.add(POD_PICTURE);
            array1.add(KORR_PICTURE);
            array1.add(LUNN_PICTURE);
            array1.add(ZVEZDA_PICTURE);
            array1.add(PON_PICTURE);
            for (String i:array1) {
                System.out.println(i);
            }*/