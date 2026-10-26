package interfaces;

import model.BookInfo;
import java.util.List;

public interface BookService {
    List<BookInfo> searchBooks(String query) throws Exception;
}