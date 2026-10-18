import java.util.ArrayList;
import java.util.List;

import console.ConsoleReader;
import console.ConsoleWriter;
import handlers.BestBooksHandler;
import handlers.GenresHandler;
import handlers.SearchHandler;
import interfaces.BotReader;
import interfaces.BotWriter;
import interfaces.CommandHandler;
// Главный класс бота
public class BookBot {
    
    private BotReader reader;
    private BotWriter writer;
    private List<CommandHandler> handlers;
    
    // Конструктор
    public BookBot(BotReader reader, BotWriter writer) {
        this.reader = reader;
        this.writer = writer;
        this.handlers = new ArrayList<>();
        
        // Регистрируем обработчики команд
        handlers.add(new GenresHandler(reader, writer));
        handlers.add(new BestBooksHandler(reader, writer));
        handlers.add(new SearchHandler(reader, writer));
    }
    // Показать главное меню
    private void showMainMenu() {
        writer.write("\n=================================");
        writer.write("  Привет! Я бот-помощник по книгам");
        writer.write("=================================");
        writer.write("1. " + handlers.get(0).getCommandName());
        writer.write("2. " + handlers.get(1).getCommandName());
        writer.write("3. " + handlers.get(2).getCommandName());
        writer.write("0. Выйти");
        writer.write("Твой выбор: ");
    }
    
    // Главный цикл работы бота
    public void start() {
        boolean running = true;
        
        while (running) {
            showMainMenu();
            String choice = reader.readInput();
            
            switch (choice) {
                case "1":
                    handlers.get(0).execute();
                    break;
                case "2":
                    handlers.get(1).execute();
                    break;
                case "3":
                    handlers.get(2).execute();
                    break;
                case "0":
                    writer.write("Пока! До новых встреч.");
                    running = false;
                    break;
                default:
                    writer.write("Неверная команда. Попробуй ещё раз.");
            }
        }
    }
    
    // Точка входа в программу
    public static void main(String[] args) {
        // Создаём читатель и писатель
        BotReader reader = new ConsoleReader();
        BotWriter writer = new ConsoleWriter();
        
        // Создаём и запускаем бота
        BookBot bot = new BookBot(reader, writer);
        bot.start();
    }
}