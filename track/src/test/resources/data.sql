
INSERT INTO users(username, password, enabled)
SELECT 'test-admin', '$2a$10$DQ9LcGuKUKua3IVfff9Eh.h4aaV.IWRRVFP5j5/qD4ZCX85Or2bom', true
WHERE NOT EXISTS (
    SELECT 1
    FROM users
    WHERE username = 'test-admin'
);

INSERT INTO users(username, password, enabled)
SELECT 'test-user', '$2a$10$AKHlbQnEX9YTtPnK.s/zr.ihMb0rTsGlv3HDbE4xmTYp1WuFOuopC', true
WHERE NOT EXISTS (
    SELECT 1
    FROM users
    WHERE username = 'test-user'
);

INSERT INTO authorities(username, authority)
SELECT 'test-admin', 'ROLE_admin'
WHERE NOT EXISTS (
    SELECT 1
    FROM authorities
    WHERE username = 'test-admin'
);

INSERT INTO authorities(username, authority)
SELECT 'test-user', 'ROLE_user'
WHERE NOT EXISTS (
    SELECT 1
    FROM authorities
    WHERE username = 'test-user'
);

INSERT INTO category(name, owner)
SELECT 'test', 'test-admin'
WHERE NOT EXISTS (
    SELECT 1
    FROM category
    WHERE name = 'test'
);

INSERT INTO timer_entry(time_tracked, owner)
SELECT 3600, 'test-admin'
WHERE NOT EXISTS (
    SELECT 1
    FROM timer_entry
    WHERE owner = 'test-admin'
);

INSERT INTO timer_entry(time_tracked, owner)
SELECT 1800, 'test-user'
WHERE NOT EXISTS (
    SELECT 1
    FROM timer_entry
    WHERE owner = 'test-user'
);

INSERT INTO timer_entry_category(category_id, timer_entry_id, owner)
SELECT c.id, te.id, 'test-admin'
FROM timer_entry te, category c
WHERE te.owner = 'test-admin'
AND c.name = 'test'
AND NOT EXISTS (
    SELECT 1
    FROM timer_entry_category
    WHERE owner = 'test-admin'
);

INSERT INTO timer_entry_category(category_id, timer_entry_id, owner)
SELECT c.id, te.id, 'test-user'
FROM timer_entry te, category c
WHERE te.owner = 'test-user'
AND c.name = 'test'
AND NOT EXISTS (
    SELECT 1
    FROM timer_entry_category
    WHERE owner = 'test-user'
);