package telegram;

import bot.BookBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Однопоточная очередь: один воркер по очереди обслуживает пользователей.
 * Пока один пользователь в сессии — остальные ждут.
 */
public class BookTelegramBot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(BookTelegramBot.class);

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
            log.info("Воркер бота запущен");
        }
    }

    private void workerLoop() {
        while (true) {
            UserTask task;
            try {
                task = taskQueue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Воркер прерван, завершаемся");
                return;
            }

            log.debug("Взята задача для чата {}", task.chatId);
            try {
                new BookBot(task.reader, task.writer).start();
            } catch (RuntimeException e) {
                log.error("Ошибка в сессии чата {}", task.chatId, e);
            } finally {
                readers.remove(task.chatId, task.reader);
                log.debug("Сессия чата {} завершена", task.chatId);
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
        log.debug("Апдейт от чата {}: {}", chatId, text);

        ensureWorkerStarted();

        if (text.equals("/start")) {
            log.info("Запрошен /start от чата {}", chatId);
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
            log.info("Новая сессия для чата {}", chatId);
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