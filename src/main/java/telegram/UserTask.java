package telegram;

/** Задача на обслуживание одного пользователя. */
public class UserTask {
    final long chatId;
    final TelegramBotReader reader;
    final TelegramBotWriter writer;

    public UserTask(long chatId, TelegramBotReader reader, TelegramBotWriter writer) {
        this.chatId = chatId;
        this.reader = reader;
        this.writer = writer;
    }
}