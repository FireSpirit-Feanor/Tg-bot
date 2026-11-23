package telegram;

import console.BotReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TelegramBotReader implements BotReader {

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
                control.markTimedOut();
                return "0";
            }
            return msg;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            control.markTimedOut();
            return "0";
        }
    }
}