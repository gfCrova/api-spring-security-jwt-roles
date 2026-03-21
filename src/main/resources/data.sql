INSERT INTO roles (description, name) VALUES ('Admin role', 'ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (description, name) VALUES ('Manager role', 'MANAGER') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (description, name) VALUES ('User role', 'USER') ON CONFLICT (name) DO NOTHING;

--INSERT INTO users (username, email, password, name, phone, business_title) values ('admin', 'user-admin@admin.com', '$2a$10$hjdjJ/M3YF.6h7fIo8PJUOjy34yMt1rF.Y3rhwAt9zJ909vXdCCu.', 'Jacob', 1199233321, 'Executive Diretor');
--INSERT INTO user_roles(user_id, role_id) values (1, 1);