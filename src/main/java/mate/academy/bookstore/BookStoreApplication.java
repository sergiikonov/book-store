package mate.academy.bookstore;

import java.math.BigDecimal;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book caught = new Book();
            caught.setTitle("Caught");
            caught.setAuthor("Liz Tomford");
            caught.setIsbn("978-3-16-148410-0");
            caught.setPrice(BigDecimal.valueOf(400));
            caught.setDescription("About alone baseball father from Chicago");
            caught.setCoverImage("https://booktop.com.ua/wp-content/uploads/2025/02/spiymana.jpg");

            bookService.save(caught);
            bookService.findAll()
                    .forEach(System.out::println);
        };
    }
}
