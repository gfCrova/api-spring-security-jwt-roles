INSERT INTO roles (description, name) VALUES ('Admin role', 'ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (description, name) VALUES ('Manager role', 'MANAGER') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (description, name) VALUES ('User role', 'USER') ON CONFLICT (name) DO NOTHING;
