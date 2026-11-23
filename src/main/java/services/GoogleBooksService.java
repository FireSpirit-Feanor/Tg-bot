package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.BookInfo;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GoogleBooksService implements BookService {

    private static final String API_KEY = System.getenv("GOOGLE_BOOKS_API_KEY");
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    private static final int MAX_RESULTS = 40;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GoogleBooksService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<BookInfo> searchBooks(String query) throws Exception {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = buildUrl("q=" + encoded);
        List<BookInfo> all = fetchBooksFromUrl(url);
        return filterByTitleSubstring(all, query);
    }

    @Override
    public List<BookInfo> getTopBooks() throws Exception {
        // Запрашиваем бестселлеры, отсортированные по релевантности
        String url = buildUrl("q=bestseller&orderBy=relevance");
        return fetchBooksFromUrl(url);
    }

    @Override
    public List<BookInfo> getBooksByGenre(String genre) throws Exception {
        // Google Books поддерживает оператор subject: для поиска по жанру
        String encoded = URLEncoder.encode(genre, StandardCharsets.UTF_8);
        String url = buildUrl("q=subject:" + encoded);
        return fetchBooksFromUrl(url);
    }

    // Общий метод: собирает URL, отправляет запрос, парсит ответ
    private String buildUrl(String params) {
        String url = BASE_URL + "?" + params + "&maxResults=" + MAX_RESULTS;
        if (API_KEY != null && !API_KEY.isEmpty()) {
            url += "&key=" + API_KEY;
        }
        return url;
    }

    private List<BookInfo> fetchBooksFromUrl(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка запроса: HTTP " + response.statusCode());
        }
        return parseBooks(response.body());
    }

    private List<BookInfo> filterByTitleSubstring(List<BookInfo> books, String substring) {
        if (substring == null || substring.isBlank()) return books;
        String needle = substring.toLowerCase();
        List<BookInfo> result = new ArrayList<>();
        for (BookInfo book : books) {
            String title = book.getTitle();
            if (title != null && title.toLowerCase().contains(needle)) {
                result.add(book);
            }
        }
        return result;
    }

    private List<BookInfo> parseBooks(String json) throws Exception {
        List<BookInfo> result = new ArrayList<>();
        JsonNode root = objectMapper.readTree(json);
        JsonNode items = root.path("items");
        if (items.isMissingNode() || !items.isArray()) return result;

        for (JsonNode item : items) {
            JsonNode volumeInfo = item.path("volumeInfo");
            String title = volumeInfo.path("title").asText("Неизвестное название");

            JsonNode authorsNode = volumeInfo.path("authors");
            String authors = "Неизвестный автор";
            if (authorsNode.isArray() && authorsNode.size() > 0) {
                List<String> list = new ArrayList<>();
                for (JsonNode a : authorsNode) list.add(a.asText());
                authors = String.join(", ", list);
            }

            JsonNode catNode = volumeInfo.path("categories");
            String categories = "Без категории";
            if (catNode.isArray() && catNode.size() > 0) {
                List<String> list = new ArrayList<>();
                for (JsonNode c : catNode) list.add(c.asText());
                categories = String.join(", ", list);
            }

            result.add(new BookInfo(title, authors, categories));
        }
        return result;
    }
}