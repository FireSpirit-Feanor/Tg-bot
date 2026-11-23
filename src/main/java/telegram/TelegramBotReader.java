package telegram;

import console.BotReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TelegramBotReader implements BotReader {

    private static final Logger log = LoggerFactory.getLogger(TelegramBotReader.class);

    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private final long idleTimeoutSeconds;
    private final SessionControl control;

    public TelegramBotReader(long idleTimeoutSeconds, SessionControl control) {
        this.idleTimeoutSeconds = idleTimeoutSeconds;
        this.control = control;
    }

    public TelegramBotReader(SessionControl control) {
        this(300, control);
    }

    public void addMessage(String text) {
        messageQueue.add(text);
    }

    @Override
    public String readInput() {
        try {
            String msg = messageQueue.poll(idleTimeoutSeconds, TimeUnit.SECONDS);
            if (msg == null) {
                log.info("Таймаут ожидания ввода ({} с), завершаем сессию", idleTimeoutSeconds);
                control.markTimedOut();
                return "0";
            }
            return msg;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Чтение прервано, завершаем сессию");
            control.markTimedOut();
            return "0";
        }
    }
}