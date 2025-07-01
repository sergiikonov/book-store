INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (999, 'test@example.com', 'password', 'Name', 'Last',
        'Address', false);

INSERT INTO users_roles (user_id, role_id) VALUES (999, 1);

INSERT INTO shopping_carts (user_id, is_deleted)
VALUES (999, false);
