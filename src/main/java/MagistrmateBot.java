import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class MagistrmateBot extends TelegramLongPollingBot {
    public static final String PZV_PICTURE = "AgACAgIAAxkBAAIBx2KTdP4CNbqTZfv7Hm7TqGAugkdSAAKIvjEbUaqZSPwL-Up482owAQADAgADeQADJAQ";
    public static final String POD_PICTURE = "AgACAgIAAxkBAAICIWKV6rrWZCfAVHeZRb600fEdhUUtAALFuTEbgvKxSGOQfTzAxJOgAQADAgADeQADJAQ";
    public static final String KORR_PICTURE = "AgACAgIAAxkBAAICHWKV6h4KSQAB0Cw-9YYzNmKqQYlBbAACwrkxG4LysUioRIR5O_3J4wEAAwIAA3kAAyQE";
    public static final String LUNN_PICTURE = "AgACAgIAAxkBAAICH2KV6lrfNbsYfnqVDQwV1uhexKUGAALDuTEbgvKxSBEP75XimlVwAQADAgADeQADJAQ";
    public static final String ZVEZDA_PICTURE = "AgACAgIAAxkBAAICG2KV6b_hGJ0jk_yHtzUWmfXrs0K-AALBuTEbgvKxSE55vwxxNxcxAQADAgADeQADJAQ";
    public static final String PON_PICTURE = "AgACAgIAAxkBAAICI2KV6u5ALDMcSPP4WPsvdr5iBJ1hAALGuTEbgvKxSGlGhmGbA1qtAQADAgADeQADJAQ";

    private static final Logger log = Logger.getLogger(Main.class);

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
        log.debug("Receive new Update. updateID: " + update.getUpdateId());
        Message message = update.getMessage();
        if  (update.hasMessage() && update.getMessage().hasText()) {
            String inputText = update.getMessage().getText();
            if (inputText.equals("/start")) {
                sendMsg(message, "Добро пожаловать " + update.getMessage().getFrom().getFirstName() + "!");
                List<InputMedia> media = new ArrayList<>();
                InputMedia photo1 = new InputMediaPhoto();
                photo1.setMedia(PZV_PICTURE);
                InputMedia photo2 = new InputMediaPhoto();
                photo2.setMedia(POD_PICTURE);
                InputMedia photo3 = new InputMediaPhoto();
                photo3.setMedia(KORR_PICTURE);
                InputMedia photo4 = new InputMediaPhoto();
                photo4.setMedia(LUNN_PICTURE);
                InputMedia photo5 = new InputMediaPhoto();
                photo5.setMedia(ZVEZDA_PICTURE);
                InputMedia photo6 = new InputMediaPhoto();
                photo6.setMedia(PON_PICTURE);
                media.add(photo1);
                media.add(photo2);
                media.add(photo3);
                media.add(photo4);
                media.add(photo5);
                media.add(photo6);
                SendMediaGroup mediaGroup = new SendMediaGroup();
                mediaGroup.setChatId(message.getChatId().toString());
                mediaGroup.setMedias(media);
                try {
                    execute(mediaGroup);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (update.getMessage().getText().toLowerCase(Locale.ROOT).contains("привет")) {
                sendMsg(message, "Дороу");
            } else
                sendMsg(message, "Даже не знаю, что на это ответить");
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
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
    }
    private void sendMsg (Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        List<KeyboardRow> keyboardRow = new ArrayList<>();
        KeyboardRow keyboard = new KeyboardRow();
        keyboard.add("Читать фрагмент");
        keyboard.add("Книги");
        keyboardRow.add(keyboard);
        replyKeyboardMarkup.setKeyboard(keyboardRow);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(sendMessage.getReplyToMessageId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        }  catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
//efw    private void sndPhoto (Message message) {
//        SendPhoto sendPhoto = new SendPhoto();
//        sendPhoto.setChatId(message.getChatId().toString());
//        sendPhoto.setPhoto(new InputFile("AgACAgIAAxkBAAIBx2KTdP4CNbqTZfv7Hm7TqGAugkdSAAKIvjEbUaqZSPwL-Up482owAQADAgADeQADJAQ"));
//        sendPhoto.setCaption("Писька");
//        try {
//            execute(sendPhoto);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
}
