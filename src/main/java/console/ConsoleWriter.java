package console;

public class ConsoleWriter implements BotWriter {
    
    @Override
    public void write(String message) {
        System.out.println(message);
    }
}