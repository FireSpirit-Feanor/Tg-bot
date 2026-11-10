package telegram;

import bot.BookBot;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BookTelegramBot extends TelegramLongPollingBot {

    // Храним сессии пользователей (поток + ридер)
    private static class UserSession {
        final TelegramBotReader reader;
        final Thread thread;

        UserSession(TelegramBotReader reader, Thread thread) {
            this.reader = reader;
            this.thread = thread;
        }
    }

    private final Map<Long, UserSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();

            UserSession session = sessions.get(chatId);

            // Если сессии нет ИЛИ поток умер (пользователь выбрал "0"), создаем заново
            if (session == null || !session.thread.isAlive()) {
                TelegramBotReader reader = new TelegramBotReader();
                TelegramBotWriter writer = new TelegramBotWriter(this, chatId);

                Thread botThread = new Thread(() -> {
                    BookBot userBot = new BookBot(reader, writer);
                    userBot.start();
                });

                session = new UserSession(reader, botThread);
                sessions.put(chatId, session);
                botThread.start();
            }

            // Передаем сообщение в очередь пользователя
            session.reader.addMessage(text);
        }
    }

    @Override
    public String getBotUsername() {
        return "Бот Ревью книг";
    }

    @Override
    public String getBotToken() {
        return System.getenv("TELEGRAM_BOT_TOKEN");
    }
}