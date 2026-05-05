ALTER TABLE users DROP COLUMN role;

ALTER TABLE user_roles DROP CONSTRAINT user_roles_pkey;
-- ALTER TABLE user_roles DROP COLUMN id;

ALTER TABLE user_roles ADD PRIMARY KEY (user_id, role_id);

ALTER TABLE user_roles DROP CONSTRAINT IF EXISTS user_roles_user_id_fkey;
ALTER TABLE user_roles ADD CONSTRAINT fk_user_roles_user
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE user_roles DROP CONSTRAINT IF EXISTS user_roles_role_id_fkey;
ALTER TABLE user_roles ADD CONSTRAINT fk_user_roles_role
FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE;