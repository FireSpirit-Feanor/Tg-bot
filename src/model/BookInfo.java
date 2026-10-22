package model;

public class BookInfo {
    private final String title;
    private final String authors;
    private final String categories;

    public BookInfo(String title, String authors, String categories) {
        this.title = title;
        this.authors = authors;
        this.categories = categories;
    }

    public String getTitle() { return title; }
    public String getAuthors() { return authors; }
    public String getCategories() { return categories; }

    @Override
    public String toString() {
        return "《" + title + "》 | Автор: " + authors + " | Жанр: " + categories;
    }
}