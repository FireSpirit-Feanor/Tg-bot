package telegram;

import console.BotReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TelegramBotReader implements BotReader {
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    public void addMessage(String text) {
        messageQueue.add(text);
    }

    @Override
    public String readInput() {
        try {
            return messageQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "0";
        }
    }
}