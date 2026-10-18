package handlers;

import interfaces.BotReader;
import interfaces.BotWriter;
import interfaces.CommandHandler;

public class GenresHandler implements CommandHandler {
    
    private BotReader reader;
    private BotWriter writer;
    
    public GenresHandler(BotReader reader, BotWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }
    
    @Override
    public String getCommandName() {
        return "Жанры";
    }
    
    @Override
    public void execute() {
        boolean back = false;
        
        while (!back) {
            writer.write("\n--- Жанры ---");
            writer.write("1. Фантастика");
            writer.write("2. Детектив");
            writer.write("3. Роман");
            writer.write("4. Фэнтези");
            writer.write("0. Назад");
            writer.write("Твой выбор: ");
            
            String choice = reader.readInput();
            
            switch (choice) {
                case "1":
                    writer.write("Ты выбрал: Фантастика");
                    break;
                case "2":
                    writer.write("Ты выбрал: Детектив");
                    break;
                case "3":
                    writer.write("Ты выбрал: Роман");
                    break;
                case "4":
                    writer.write("Ты выбрал: Фэнтези");
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    writer.write("Неверная команда.");
            }
        }
    }
}