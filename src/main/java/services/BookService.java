package services;

import model.BookInfo;
import java.util.List;

public interface BookService {
    List<BookInfo> searchBooks(String query) throws Exception;
    List<BookInfo> getTopBooks() throws Exception;
    List<BookInfo> getBooksByGenre(String genre) throws Exception;
}