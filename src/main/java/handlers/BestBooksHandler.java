package handlers;

import console.BotReader;
import console.BotWriter;
import model.BookInfo;
import services.BookService;

import java.util.List;

public class BestBooksHandler implements CommandHandler {

    private final BotReader reader;
    private final BotWriter writer;
    private final BookService bookService;

    public BestBooksHandler(BotReader reader, BotWriter writer, BookService bookService) {
        this.reader = reader;
        this.writer = writer;
        this.bookService = bookService;
    }

    @Override
    public String getCommandName() {
        return "Лучшие книги";
    }

    @Override
    public void execute() {
        boolean back = false;

        while (!back) {
            List<BookInfo> topBooks;
            try {
                writer.write("\nЗагружаем топ книг с Google Books...");
                topBooks = bookService.getTopBooks();
            } catch (Exception e) {
                writer.write("Не удалось загрузить книги: " + e.getMessage());
                return;
            }

            if (topBooks.isEmpty()) {
                writer.write("Список пуст.");
                return;
            }

            writer.write("\n--- Топ книг (по рейтингу) ---");
            // Динамическое меню: сколько книг вернул API, столько и покажем
            int limit = Math.min(topBooks.size(), 10);
            for (int i = 0; i < limit; i++) {
                writer.write((i + 1) + ". " + topBooks.get(i));
            }
            writer.write("0. Назад");
            writer.write("Выберите книгу (или 0): ");

            String choice = reader.readInput().trim();

            if (choice.equals("0")) {
                back = true;
            } else {
                try {
                    int index = Integer.parseInt(choice) - 1;
                    if (index >= 0 && index < limit) {
                        BookInfo b = topBooks.get(index);
                        writer.write("\n📚 " + b.getTitle());
                        writer.write("Автор: " + b.getAuthors());
                        writer.write("Жанр: " + b.getCategories());
                    } else {
                        writer.write("Такого номера нет.");
                    }
                } catch (NumberFormatException e) {
                    writer.write("Введите число.");
                }
            }
        }
    }
}