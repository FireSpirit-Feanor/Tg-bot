package handlers;

import interfaces.BotReader;
import interfaces.BotWriter;
import interfaces.CommandHandler;

public class BestBooksHandler implements CommandHandler {
    
    private BotReader reader;
    private BotWriter writer;
    
    public BestBooksHandler(BotReader reader, BotWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }
    
    @Override
    public String getCommandName() {
        return "Лучшие книги";
    }
    
    @Override
    public void execute() {
        boolean back = false;
        
        while (!back) {
            writer.write("\n--- Лучшие книги ---");
            writer.write("Здесь будет топ книг по рейтингу");
            // TODO: список лучших книг
            writer.write("0. Назад");
            writer.write("Твой выбор: ");
            
            String choice = reader.readInput();
            
            if (choice.equals("0")) {
                back = true;
            } else {
                writer.write("Неверная команда.");
            }
        }
    }
}