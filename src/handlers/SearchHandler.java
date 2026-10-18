package handlers;

import interfaces.BotReader;
import interfaces.BotWriter;
import interfaces.CommandHandler;

public class SearchHandler implements CommandHandler {
    
    private BotReader reader;
    private BotWriter writer;
    
    public SearchHandler(BotReader reader, BotWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }
    
    @Override
    public String getCommandName() {
        return "Поиск по названию";
    }
    
    @Override
    public void execute() {
        boolean back = false;
        
        while (!back) {
            writer.write("\n--- Поиск по названию ---");
            writer.write("Введите название книги (или '0' чтобы вернуться): ");
            
            String input = reader.readInput();
            
            if (input.equals("0")) {
                back = true;
            } else {
                writer.write("Ищем книгу: \"" + input + "\"...");
                // TODO: реальный поиск
                writer.write("Ничего не найдено (пока что).");
            }
        }
    }
}