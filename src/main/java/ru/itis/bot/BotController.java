package ru.itis.bot;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.itis.bot.models.User;
import ru.itis.bot.services.UserServiceImpl;

@Component
public class BotController extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(BotController.class);

    @Autowired
    private Environment env;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public String getBotToken() {
        return env.getProperty("bot.token");
    }

    @Override
    public String getBotUsername() {
        return env.getProperty("bot.username");
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String text = message.getText();
            Long chatId = message.getChatId();
            if(text.startsWith("/start") || (text.startsWith("/help") )){
                sendHelpMessage(chatId);
            }
            else {

                String[] commands = text.trim().split(",");

                int length = commands.length;
                if (commands[0].equals("/add") && length==4) {
                    String name = commands[1].trim();
                    String city = commands[2].trim();
                    String date = commands[3].trim();
                    addUser(chatId, name, city, date);
                } else if (commands[0].equals("/add")){
                    errorResponse(chatId);
                }

                if (commands[0].equals("/get") && length==2) {
                    String name = commands[1].trim();
                    getUser(chatId, name);
                } else if(commands[0].equals("/get")) {
                    errorResponse(chatId);
                }

                if (commands[0].equals("/update") && length==4) {
                    String name = commands[1].trim();
                    String newCity = commands[2].trim();
                    String newDate = commands[3].trim();
                    userService.updateUser(name, newCity, newDate);
                    SendMessage response = new SendMessage();
                    text = "Record updated successfully";
                    response.setChatId(chatId);
                    response.setText(text);
                    try {
                        execute(response);
                        logger.info("Sent message \"{}\" to {}", text, chatId);
                    } catch (TelegramApiException e) {
                        logger.error("Failed to send message \"{}\" to {} due to error: {}", text, chatId, e.getMessage());
                    }

                } else if (commands[0].equals("/update")){
                    errorResponse(chatId);
                }

                if (commands[0].equals("/delete") && length==2 && !userService.getUserByName(commands[1]).equals(null)) {
                    String name = commands[1].trim();
                    userService.deleteUser(name);
                    SendMessage response = new SendMessage();
                    text = "Record deleted successfully";
                    response.setChatId(chatId);
                    response.setText(text);
                    try {
                        execute(response);
                        logger.info("Sent message \"{}\" to {}", text, chatId);
                    } catch (TelegramApiException e) {
                        logger.error("Failed to send message \"{}\" to {} due to error: {}", text, chatId, e.getMessage());
                    }
                } else if (commands[0].equals("/delete")) {
                    errorResponse(chatId);
                }

                else if (!(commands[0].equals("/update") || commands[0].equals("/add") || commands[0].equals("/get") || commands[0].equals("/delete"))){
                    System.out.println("TEST TEST TEST");
                    errorResponse(chatId);
                }
            }
        }
    }

    private void sendHelpMessage(Long chatId) {
        SendMessage response = new SendMessage();
        String text = "Hello! Please, use next commands and don't forget about commas!\n" +
                " /get, Full name \n" +
                " /add, Full name,City,Birthday date \n" +
                "/update, Full name of person You want to update, newCity, newDate\n" +
                "/delete, Full name of person You want to delete ";
        response.setChatId(chatId);
        response.setText(text);
        try {
            execute(response);
            logger.info("Sent message \"{}\" to {}", text, chatId);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message \"{}\" to {} due to error: {}", text, chatId, e.getMessage());
        }
    }

    private void addUser(Long chatId, String name, String city, String dateOfBirth) {
        User user = User.builder().name(name).city(city).date(dateOfBirth).build();
        userService.addUser(user);
        SendMessage response = new SendMessage();
        String text = "Successfully added to notebook";
        response.setChatId(chatId);
        response.setText(text);
        try {
            execute(response);
            logger.info("Sent message \"{}\" to {}", text, chatId);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message \"{}\" to {} due to error: {}", text, chatId, e.getMessage());
        }
    }

    @SneakyThrows
    private void getUser(Long chatId, String name) {
        SendMessage response = new SendMessage();
        User user = userService.getUserByName(name);
        response.setChatId(chatId);
        if(user==null){
            String text = "There is no such record.";
            response.setText(text);
            execute(response);

        }
        else {
            String text = user.toString();
            response.setText(text);
            try {
                execute(response);
                logger.info("Sent message \"{}\" to {}", text, chatId);
            } catch (TelegramApiException e) {
                logger.error("Failed to send message \"{}\" to {} due to error: {}", text, chatId, e.getMessage());
            }
        }
    }

    private void errorResponse(Long chatId) {
        SendMessage response = new SendMessage();
        response.setChatId(chatId);
        String text = "Error in command, see /help";
        response.setText(text);
        try {
            execute(response);
            logger.info("Sent message \"{}\" to {}", text, chatId);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message \"{}\" to {} due to error: {}", text, chatId, e.getMessage());
        }
    }
}
