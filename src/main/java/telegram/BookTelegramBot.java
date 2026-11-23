package telegram;

import bot.BookBot;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 Однопоточная очередь: по очереди обслуживает пользователей.
 Пока один пользователь в сессии — остальные ждут, их сообщения копятся в очередях соответствующих TelegramBotReader.
 */
public class BookTelegramBot extends TelegramLongPollingBot {

    private final BlockingQueue<UserTask> taskQueue = new LinkedBlockingQueue<>();
    private final Map<Long, TelegramBotReader> readers = new ConcurrentHashMap<>();
    private final AtomicBoolean workerStarted = new AtomicBoolean(false);
    public BookTelegramBot() {
        super();
    }

    public BookTelegramBot(DefaultBotOptions options) {
        super(options);
    }

    private void ensureWorkerStarted() {
        if (workerStarted.compareAndSet(false, true)) {
            Thread worker = new Thread(this::workerLoop, "book-bot-worker");
            worker.setDaemon(true);
            worker.start();
        }
    }

    private void workerLoop() {
        while (true) {
            UserTask task;
            try {
                task = taskQueue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            try {
                new BookBot(task.reader, task.writer).start();
            } catch (RuntimeException e) {
                System.err.println("Ошибка в сессии чата " + task.chatId + ": " + e.getMessage());
            } finally {
                readers.remove(task.chatId, task.reader);
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        ensureWorkerStarted();

        if (text.equals("/start")) {
            TelegramBotReader existing = readers.remove(chatId);
            if (existing != null) {
                existing.addMessage("0");
            }
            SessionControl control = new SessionControl();
            TelegramBotReader reader = new TelegramBotReader(control);
            TelegramBotWriter writer = new TelegramBotWriter(this, chatId, control);
            readers.put(chatId, reader);
            taskQueue.add(new UserTask(chatId, reader, writer));
            return;
        }

        TelegramBotReader reader = readers.get(chatId);
        if (reader == null) {
            SessionControl control = new SessionControl();
            TelegramBotReader newReader = new TelegramBotReader(control);
            TelegramBotWriter writer = new TelegramBotWriter(this, chatId, control);
            readers.put(chatId, newReader);
            newReader.addMessage(text);
            taskQueue.add(new UserTask(chatId, newReader, writer));
        } else {
            reader.addMessage(text);
        }
    }

    @Override
    public String getBotUsername() {
        return "review_books_bot";
    }

    @Override
    public String getBotToken() {
        return System.getenv("TELEGRAM_BOT_TOKEN");
    }
}