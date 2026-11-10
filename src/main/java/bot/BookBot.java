package bot;

import console.BotReader;
import console.BotWriter;
import handlers.CommandHandler;
import handlers.BestBooksHandler;
import handlers.GenresHandler;
import handlers.SearchHandler;
import services.BookService;
import services.GoogleBooksService;

import java.util.LinkedHashMap;
import java.util.Map;

public class BookBot {

    private final BotReader reader;
    private final BotWriter writer;
    private final Map<Integer, CommandHandler> handlers;

    public BookBot(BotReader reader, BotWriter writer) {
        this.reader = reader;
        this.writer = writer;
        this.handlers = new LinkedHashMap<>();

        BookService bookService = new GoogleBooksService();

        handlers.put(1, new GenresHandler(reader, writer));
        handlers.put(2, new BestBooksHandler(reader, writer));
        handlers.put(3, new SearchHandler(reader, writer, bookService));
    }

    private void showMainMenu() {
        writer.write("\n=================================");
        writer.write("  Привет! Я бот-помощник по книгам");
        writer.write("=================================");

        for (Map.Entry<Integer, CommandHandler> entry : handlers.entrySet()) {
            writer.write(entry.getKey() + ". " + entry.getValue().getCommandName());
        }
        writer.write("0. Выйти");
        writer.write("Твой выбор: ");
    }

    public void start() {
        boolean running = true;

        while (running) {
            showMainMenu();
            String choice = reader.readInput().trim();

            if (choice.equals("0")) {
                writer.write("Пока! До новых встреч.");
                running = false;
            } else {
                try {
                    int commandId = Integer.parseInt(choice);
                    CommandHandler handler = handlers.get(commandId);

                    if (handler != null) {
                        handler.execute();
                    } else {
                        writer.write("Неверная команда. Попробуй ещё раз.");
                    }
                } catch (NumberFormatException e) {
                    writer.write("Пожалуйста, введите число.");
                }
            }
        }
    }
}