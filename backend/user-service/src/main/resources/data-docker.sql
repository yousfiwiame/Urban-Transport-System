-- ============================================
-- DONNÉES DE TEST POUR USER-SERVICE
-- ============================================

-- 1. Insertion d'utilisateurs de test
-- Mot de passe pour tous : "password123" (encodé en BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy)
INSERT INTO users (
    email,
    password,
    first_name,
    last_name,
    phone_number,
    status,
    email_verified,
    phone_verified,
    enabled,
    account_non_locked,
    failed_login_attempts,
    auth_provider,
    created_at,
    updated_at
) VALUES
    ('hiibaalaoui@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Hiba', 'Alaoui', '+212600111111', 'ACTIVE', true, true, true, true, 0, 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('mohammed.alami@test.ma', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Mohammed', 'Alami', '+212600222222', 'ACTIVE', true, true, true, true, 0, 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('fatima.zahra@test.ma', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Fatima', 'Zahra', '+212600333333', 'ACTIVE', true, false, true, true, 0, 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ahmed.benani@driver.ma', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ahmed', 'Benani', '+212600444444', 'ACTIVE', true, true, true, true, 0, 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('youssef.tazi@test.ma', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Youssef', 'Tazi', '+212600555555', 'ACTIVE', true, true, true, true, 0, 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

-- 2. Association des rôles aux utilisateurs
-- Hiba = ADMIN + PASSENGER
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'hiibaalaoui@gmail.com' AND r.name = 'ADMIN'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'hiibaalaoui@gmail.com' AND r.name = 'PASSENGER'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Autres utilisateurs = PASSENGER
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email IN ('mohammed.alami@test.ma', 'fatima.zahra@test.ma', 'youssef.tazi@test.ma')
AND r.name = 'PASSENGER'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Driver
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'ahmed.benani@driver.ma' AND r.name = 'DRIVER'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- 3. Création des profils utilisateurs
-- Profil de Hiba
INSERT INTO user_profiles (
    user_id,
    date_of_birth,
    gender,
    address,
    city,
    country,
    postal_code,
    nationality,
    occupation,
    preferred_language,
    notifications_enabled,
    email_notifications_enabled,
    sms_notifications_enabled,
    push_notifications_enabled,
    created_at,
    updated_at
)
SELECT
    u.id,
    '2000-01-15'::date,
    'FEMALE',
    'Internat ENSIAS, Avenue Mohammed Ben Abdallah Regragui',
    'Rabat',
    'Morocco',
    '10100',
    'Moroccan',
    'Computer Science Student',
    'fr',
    true,
    true,
    true,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM users u WHERE u.email = 'hiibaalaoui@gmail.com'
ON CONFLICT (user_id) DO NOTHING;

-- Profil de Mohammed
INSERT INTO user_profiles (
    user_id,
    date_of_birth,
    gender,
    address,
    city,
    country,
    postal_code,
    nationality,
    occupation,
    preferred_language,
    notifications_enabled,
    email_notifications_enabled,
    created_at,
    updated_at
)
SELECT
    u.id,
    '1990-07-22'::date,
    'MALE',
    '456 Avenue Hassan II',
    'Rabat',
    'Morocco',
    '10000',
    'Moroccan',
    'Software Engineer',
    'fr',
    true,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM users u WHERE u.email = 'mohammed.alami@test.ma'
ON CONFLICT (user_id) DO NOTHING;

-- Profil de Fatima
INSERT INTO user_profiles (
    user_id,
    date_of_birth,
    gender,
    address,
    city,
    country,
    postal_code,
    nationality,
    occupation,
    preferred_language,
    notifications_enabled,
    push_notifications_enabled,
    created_at,
    updated_at
)
SELECT
    u.id,
    '1995-11-10'::date,
    'FEMALE',
    '789 Rue de Fès',
    'Marrakech',
    'Morocco',
    '40000',
    'Moroccan',
    'Teacher',
    'ar',
    true,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM users u WHERE u.email = 'fatima.zahra@test.ma'
ON CONFLICT (user_id) DO NOTHING;

-- Profil du chauffeur Ahmed
INSERT INTO user_profiles (
    user_id,
    date_of_birth,
    gender,
    address,
    city,
    country,
    postal_code,
    nationality,
    occupation,
    preferred_language,
    notifications_enabled,
    email_notifications_enabled,
    sms_notifications_enabled,
    created_at,
    updated_at
)
SELECT
    u.id,
    '1982-05-30'::date,
    'MALE',
    '321 Boulevard Zerktouni',
    'Casablanca',
    'Morocco',
    '20100',
    'Moroccan',
    'Bus Driver',
    'fr',
    true,
    true,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM users u WHERE u.email = 'ahmed.benani@driver.ma'
ON CONFLICT (user_id) DO NOTHING;

-- Profil de Youssef
INSERT INTO user_profiles (
    user_id,
    date_of_birth,
    gender,
    address,
    city,
    country,
    postal_code,
    nationality,
    preferred_language,
    notifications_enabled,
    created_at,
    updated_at
)
SELECT
    u.id,
    '1998-12-05'::date,
    'MALE',
    '555 Avenue Mohammed VI',
    'Tangier',
    'Morocco',
    '90000',
    'Moroccan',
    'ar',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM users u WHERE u.email = 'youssef.tazi@test.ma'
ON CONFLICT (user_id) DO NOTHING;