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
    private static final Logger log = Logger.getLogger(Main.class);

    @Override
    public String getBotUsername() {
        return "Magistrmate_bot";
    }

    @Override
    public String getBotToken() {
        return "5209127731:AAEHGrz4hmtgmbOtrprHiIuROrCGm04KK4k";
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Receive new Update. updateID: " + update.getUpdateId());
        Message message = update.getMessage();
        if  (update.hasMessage() && update.getMessage().hasText()) {
            String inputText = update.getMessage().getText();
            if (inputText.equals("/start")) {
                sendMsg(message, "Добро пожаловать " + update.getMessage().getFrom().getFirstName() + "!");
                //sndPhoto(message);
                List<InputMedia> media = new ArrayList<>();
                InputMedia photo1 = new InputMediaPhoto();
                photo1.setMedia("AgACAgIAAxkBAAIBx2KTdP4CNbqTZfv7Hm7TqGAugkdSAAKIvjEbUaqZSPwL-Up482owAQADAgADeQADJAQ");
                InputMedia photo2 = new InputMediaPhoto();
                photo2.setMedia("AgACAgIAAxkBAAIBzWKTeujFc3jBbsjGDvAz-mRzBjagAALiuTEb5HCYSCsiIM87y116AQADAgADeQADJAQ");
                media.add(photo1);
                media.add(photo2);
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
                // Message contains photo
                // Set variables
                long chat_id = update.getMessage().getChatId();

                // Array with photo objects with different sizes
                // We will get the biggest photo from that array
                List<PhotoSize> photos = update.getMessage().getPhoto();
                // Know file_id
                String f_id = Objects.requireNonNull(photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                        .orElse(null)).getFileId();
                // Set photo caption
                String caption = "file_id: " + f_id;
                SendPhoto msg = new SendPhoto();
                msg.setPhoto(new InputFile(f_id));
                msg.setChatId(String.valueOf(chat_id));
                msg.setCaption(caption);

                try {
                    execute(msg); // Call method to send the photo with caption
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
        }
    }
    private void sendMsg (Message message, String text) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

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
