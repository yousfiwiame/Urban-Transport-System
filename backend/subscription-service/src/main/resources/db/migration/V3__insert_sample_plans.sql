-- Insert sample subscription plans for testing

-- Delete existing plans if any (for development only)
-- TRUNCATE TABLE subscription_plan CASCADE;

-- Monthly Plan
INSERT INTO subscription_plan (plan_id, plan_code, plan_name, description, features, duration_days, price, currency, is_active, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'MONTHLY',
    'Abonnement Mensuel',
    'Accès illimité pendant 30 jours à tous les bus et lignes',
    '["Accès illimité", "Toutes les lignes", "Support standard", "Remboursement partiel"]'::jsonb,
    30,
    150.00,
    'MAD',
    true,
    NOW(),
    NOW()
) ON CONFLICT (plan_code) DO NOTHING;

-- Annual Plan
INSERT INTO subscription_plan (plan_id, plan_code, plan_name, description, features, duration_days, price, currency, is_active, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'ANNUAL',
    'Abonnement Annuel',
    'Accès illimité pendant 365 jours avec 20% de réduction',
    '["Accès illimité", "Toutes les lignes", "Support premium 24/7", "Priorité embarquement", "Remboursement total"]'::jsonb,
    365,
    1500.00,
    'MAD',
    true,
    NOW(),
    NOW()
) ON CONFLICT (plan_code) DO NOTHING;

-- Weekly Plan
INSERT INTO subscription_plan (plan_id, plan_code, plan_name, description, features, duration_days, price, currency, is_active, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'WEEKLY',
    'Abonnement Hebdomadaire',
    'Accès illimité pendant 7 jours, idéal pour les visiteurs',
    '["Accès illimité", "Toutes les lignes", "Support standard"]'::jsonb,
    7,
    45.00,
    'MAD',
    true,
    NOW(),
    NOW()
) ON CONFLICT (plan_code) DO NOTHING;
