import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyAmazingBot extends TelegramLongPollingBot {
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
        String inputText = update.getMessage().getText();

        Message message = update.getMessage();
        if (inputText.startsWith("/start")) {
            sendMsg(message, "Добро пожаловать " + update.getMessage().getFrom().getFirstName() + "!");
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().toLowerCase(Locale.ROOT).contains("привет")) {
                sendMsg(message, "Дороу");
            } else {
                sendMsg(message, "Даже не знаю, что на это ответить");
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
        keyboard.add("Name");
        keyboard.add("Add");
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
}
