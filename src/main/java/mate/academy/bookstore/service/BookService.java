package mate.academy.bookstore.service;

import java.util.List;
import mate.academy.bookstore.model.Book;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
