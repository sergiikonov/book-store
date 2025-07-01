INSERT INTO categories (id, name, description, is_deleted)
VALUES (1, 'Name', 'Description', false);

INSERT INTO books (id, title, cover_image, isbn, author, price, description, is_deleted)
VALUES (1, 'Title', 'Cover', '1010101', 'Author', 10.00, 'Description', false);

INSERT INTO books_categories (book_id, category_id)
VALUES (1, 1);
