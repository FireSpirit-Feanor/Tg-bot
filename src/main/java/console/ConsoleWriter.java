package console;

import interfaces.BotWriter;

public class ConsoleWriter implements BotWriter {
    
    @Override
    public void write(String message) {
        System.out.println(message);
    }
}