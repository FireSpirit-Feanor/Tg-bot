package handlers;

import console.BotReader;
import console.BotWriter;
import model.BookInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.BookService;

import java.util.List;

public class SearchHandler implements CommandHandler {

    private static final Logger log = LoggerFactory.getLogger(SearchHandler.class);

    private final BotReader reader;
    private final BotWriter writer;
    private final BookService bookService;

    public SearchHandler(BotReader reader, BotWriter writer, BookService bookService) {
        this.reader = reader;
        this.writer = writer;
        this.bookService = bookService;
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

            String input = reader.readInput().trim();
            if (input.equals("0")) { back = true; continue; }
            if (input.isEmpty()) { writer.write("Пустой запрос. Попробуйте снова."); continue; }

            writer.write("Ищем книгу: \"" + input + "\"...");
            log.info("Поиск книги: '{}'", input);

            try {
                List<BookInfo> books = bookService.searchBooks(input);
                if (books.isEmpty()) {
                    writer.write("Ничего не найдено.");
                } else {
                    writer.write("Найдено результатов: " + books.size());
                    int limit = Math.min(books.size(), 5);
                    for (int i = 0; i < limit; i++) {
                        writer.write((i + 1) + ". " + books.get(i));
                    }
                    if (books.size() > limit) {
                        writer.write("... и ещё " + (books.size() - limit));
                    }
                }
            } catch (Exception e) {
                log.error("Ошибка поиска по запросу '{}'", input, e);
                writer.write("Не удалось выполнить поиск: "
                        + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
            }
        }
    }
}