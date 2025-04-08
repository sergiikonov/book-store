package mate.academy.bookstore.dto;

public record BookSearchParameters(String[] authors, String[] titles, String[] isbns) {
}
