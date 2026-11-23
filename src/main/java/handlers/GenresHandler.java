package handlers;

import console.BotReader;
import console.BotWriter;
import model.BookInfo;
import services.BookService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GenresHandler implements CommandHandler {

    private final BotReader reader;
    private final BotWriter writer;
    private final BookService bookService;

    // Список жанров — справочник. Ключ = название, значение = запрос к API
    private final Map<String, String> genres = new LinkedHashMap<>();

    public GenresHandler(BotReader reader, BotWriter writer, BookService bookService) {
        this.reader = reader;
        this.writer = writer;
        this.bookService = bookService;

        genres.put("Фантастика", "Science Fiction");
        genres.put("Детектив", "Detective and mystery stories");
        genres.put("Роман", "Fiction");
        genres.put("Фэнтези", "Fantasy");
        genres.put("Приключения", "Adventure");
        genres.put("История", "History");
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
            int i = 1;
            for (String g : genres.keySet()) {
                writer.write(i + ". " + g);
                i++;
            }
            writer.write("0. Назад");
            writer.write("Твой выбор: ");

            String choice = reader.readInput().trim();

            if (choice.equals("0")) {
                back = true;
            } else {
                try {
                    int index = Integer.parseInt(choice) - 1;
                    List<String> keys = new java.util.ArrayList<>(genres.keySet());
                    if (index >= 0 && index < keys.size()) {
                        String genreName = keys.get(index);
                        String apiQuery = genres.get(genreName);
                        writer.write("\nИщем книги в жанре: " + genreName + "...");
                        try {
                            List<BookInfo> books = bookService.getBooksByGenre(apiQuery);
                            if (books.isEmpty()) {
                                writer.write("Ничего не найдено.");
                            } else {
                                writer.write("Найдено: " + books.size());
                                int limit = Math.min(books.size(), 5);
                                for (int j = 0; j < limit; j++) {
                                    writer.write((j + 1) + ". " + books.get(j));
                                }
                            }
                        } catch (Exception e) {
                            writer.write("Ошибка поиска: " + e.getMessage());
                        }
                    } else {
                        writer.write("Такого жанра нет.");
                    }
                } catch (NumberFormatException e) {
                    writer.write("Введите число.");
                }
            }
        }
    }
}