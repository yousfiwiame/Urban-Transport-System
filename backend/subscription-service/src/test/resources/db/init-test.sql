-- Script SQL pour les tests avec TestContainers
-- Utilise VARCHAR au lieu d'ENUMs PostgreSQL (compatible avec notre migration)

-- Extensions
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ===== FUNCTION updated_at =====
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS trigger AS $$
BEGIN
  NEW.updated_at := now();
  RETURN NEW;
END$$ LANGUAGE plpgsql;

-- ===== TABLE: subscription_plan =====
CREATE TABLE subscription_plan (
  plan_id        uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  plan_code      varchar(64) NOT NULL UNIQUE,
  description    text,
  duration_days  int NOT NULL CHECK (duration_days > 0),
  price          numeric(10,2) NOT NULL CHECK (price >= 0),
  currency       char(3) NOT NULL,
  is_active      boolean NOT NULL DEFAULT true,
  created_at     timestamptz NOT NULL DEFAULT now(),
  updated_at     timestamptz NOT NULL DEFAULT now()
);

CREATE TRIGGER trg_plan_updated_at
BEFORE UPDATE ON subscription_plan
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ===== TABLE: subscription =====
CREATE TABLE subscription (
  subscription_id    uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id            uuid NOT NULL,
  plan_id            uuid NOT NULL REFERENCES subscription_plan(plan_id) ON DELETE RESTRICT,
  status             varchar(20) NOT NULL DEFAULT 'PENDING' 
    CHECK (status IN ('PENDING','ACTIVE','PAUSED','CANCELLED','EXPIRED')),
  start_date         date NOT NULL,
  end_date           date,
  next_billing_date  date,
  amount_paid        numeric(10,2) NOT NULL DEFAULT 0 CHECK (amount_paid >= 0),
  auto_renew_enabled boolean NOT NULL DEFAULT true,
  card_token         varchar(128),
  card_brand         varchar(50),
  card_last4         varchar(4),
  card_exp_month     int CHECK (card_exp_month BETWEEN 1 AND 12),
  card_exp_year      int,
  qr_code_data       text,
  created_at         timestamptz NOT NULL DEFAULT now(),
  updated_at         timestamptz NOT NULL DEFAULT now(),
  deleted_at         timestamptz
);

CREATE TRIGGER trg_sub_updated_at
BEFORE UPDATE ON subscription
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ===== TABLE: subscription_payment =====
CREATE TABLE subscription_payment (
  payment_id       uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  subscription_id  uuid NOT NULL REFERENCES subscription(subscription_id) ON DELETE CASCADE,
  amount           numeric(10,2) NOT NULL CHECK (amount > 0),
  currency         char(3) NOT NULL,
  payment_status   varchar(20) NOT NULL DEFAULT 'PENDING'
    CHECK (payment_status IN ('PENDING','SUCCEEDED','FAILED','REFUNDED','CANCELLED')),
  payment_method   varchar(20) NOT NULL
    CHECK (payment_method IN ('CARD','BANK_TRANSFER','WALLET','CASH','OTHER')),
  payment_type     varchar(20) NOT NULL DEFAULT 'INITIAL'
    CHECK (payment_type IN ('INITIAL','RENEWAL','UPGRADE','DOWNGRADE','ADJUSTMENT','REFUND')),
  payment_date     timestamptz NOT NULL DEFAULT now(),
  failure_reason   text,
  external_txn_id  varchar(128),
  idempotency_key  varchar(128) UNIQUE,
  created_at       timestamptz NOT NULL DEFAULT now()
);

-- ===== TABLE: subscription_history =====
CREATE TABLE subscription_history (
  history_id       uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  subscription_id  uuid NOT NULL REFERENCES subscription(subscription_id) ON DELETE CASCADE,
  old_status       varchar(20) 
    CHECK (old_status IN ('PENDING','ACTIVE','PAUSED','CANCELLED','EXPIRED')),
  new_status       varchar(20) NOT NULL
    CHECK (new_status IN ('PENDING','ACTIVE','PAUSED','CANCELLED','EXPIRED')),
  event_type       varchar(64) NOT NULL,
  event_date       timestamptz NOT NULL DEFAULT now(),
  performed_by     uuid,
  details          text,
  metadata         jsonb
);

-- ===== INDEXES =====
CREATE INDEX idx_subscription_plan_id   ON subscription(plan_id);
CREATE INDEX idx_subscription_user_id   ON subscription(user_id);
CREATE INDEX idx_subscription_status    ON subscription(status);
CREATE INDEX idx_subscription_next_billing ON subscription(next_billing_date);
CREATE INDEX idx_subscription_deleted_at ON subscription(deleted_at);
CREATE INDEX idx_payment_subscription   ON subscription_payment(subscription_id);
CREATE INDEX idx_payment_status         ON subscription_payment(payment_status);
CREATE INDEX idx_payment_external_txn   ON subscription_payment(external_txn_id);
CREATE INDEX idx_payment_idempotency    ON subscription_payment(idempotency_key);
CREATE INDEX idx_history_subscription   ON subscription_history(subscription_id);
CREATE INDEX idx_history_event_date      ON subscription_history(event_date);

-- Règle métier: un seul abonnement ACTIF par (user_id, plan_id)
CREATE UNIQUE INDEX uq_active_subscription_per_user_plan
ON subscription(user_id, plan_id)
WHERE status = 'ACTIVE' AND deleted_at IS NULL;

