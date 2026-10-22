package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import interfaces.BookService;
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

    private static final String API_KEY = "AIzaSyASQf5eYBl8H1ewOAZ_W8ynTSfxv39xwy8";
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GoogleBooksService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<BookInfo> searchBooks(String query) throws Exception {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = BASE_URL + "?q=" + encodedQuery;
        if (!API_KEY.isEmpty()) {
            url += "&key=" + API_KEY;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка запроса: HTTP " + response.statusCode());
        }

        return parseBooks(response.body());
    }

    private List<BookInfo> parseBooks(String json) throws Exception {
        List<BookInfo> result = new ArrayList<>();
        JsonNode root = objectMapper.readTree(json);
        JsonNode items = root.path("items");

        if (items.isMissingNode() || !items.isArray()) {
            return result;
        }

        for (JsonNode item : items) {
            JsonNode volumeInfo = item.path("volumeInfo");

            String title = volumeInfo.path("title").asText("Неизвестное название");

            JsonNode authorsNode = volumeInfo.path("authors");
            String authors;
            if (authorsNode.isArray() && authorsNode.size() > 0) {
                List<String> authorList = new ArrayList<>();
                for (JsonNode author : authorsNode) {
                    authorList.add(author.asText());
                }
                authors = String.join(", ", authorList);
            } else {
                authors = "Неизвестный автор";
            }

            JsonNode categoriesNode = volumeInfo.path("categories");
            String categories;
            if (categoriesNode.isArray() && categoriesNode.size() > 0) {
                List<String> catList = new ArrayList<>();
                for (JsonNode cat : categoriesNode) {
                    catList.add(cat.asText());
                }
                categories = String.join(", ", catList);
            } else {
                categories = "Без категории";
            }

            result.add(new BookInfo(title, authors, categories));
        }

        return result;
    }
}