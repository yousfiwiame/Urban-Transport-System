-- ===== EXTENSIONS =====
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ===== TABLE: notification_template =====
CREATE TABLE notification_template (
  template_id          INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  template_code        VARCHAR(50) NOT NULL UNIQUE,
  channel_type         VARCHAR(20) NOT NULL CHECK (channel_type IN ('EMAIL','SMS','PUSH','WEBHOOK')),
  subject              VARCHAR(255),
  body_template        TEXT NOT NULL,
  is_active            BOOLEAN NOT NULL DEFAULT TRUE,
  created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_template_code ON notification_template(template_code);
CREATE INDEX idx_template_channel ON notification_template(channel_type);
CREATE INDEX idx_template_active ON notification_template(is_active);

-- ===== TABLE: notification_event =====
CREATE TABLE notification_event (
  event_id             INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  event_type           VARCHAR(64)  NOT NULL,
  source_service       VARCHAR(64)  NOT NULL,
  correlation_id       VARCHAR(128),
  processing_status    VARCHAR(16)  NOT NULL DEFAULT 'RECEIVED',
  received_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  processed_at         TIMESTAMPTZ
);

-- Helpful lookups
CREATE INDEX idx_event_type ON notification_event(event_type);
CREATE INDEX idx_event_correlation ON notification_event(correlation_id);
CREATE INDEX idx_event_status ON notification_event(processing_status);
CREATE INDEX idx_event_source ON notification_event(source_service);

-- ===== TABLE: user_notification_preference =====
CREATE TABLE user_notification_preference (
  preference_id        INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  user_id              INT  NOT NULL UNIQUE,
  email_enabled        BOOLEAN NOT NULL DEFAULT TRUE,
  email_address        VARCHAR(320),
  sms_enabled          BOOLEAN NOT NULL DEFAULT FALSE,
  phone_number         VARCHAR(32),
  push_enabled         BOOLEAN NOT NULL DEFAULT FALSE,
  push_tokens          JSONB,
  do_not_disturb_start TIMESTAMPTZ,
  do_not_disturb_end   TIMESTAMPTZ,
  updated_at           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_preference_user_id ON user_notification_preference(user_id);

-- ===== TABLE: notification =====
CREATE TABLE notification (
  notification_id      INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  user_id              INT NOT NULL,
  title                VARCHAR(255) NOT NULL,
  message_body         TEXT NOT NULL,
  status               VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                       CHECK (status IN ('PENDING','SENDING','SENT','DELIVERED','FAILED','READ')),
  scheduled_at         TIMESTAMPTZ,
  sent_at              TIMESTAMPTZ,
  read_at              TIMESTAMPTZ,
  created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  event_id             INT,
  template_id          INT,
  CONSTRAINT fk_notification_event
    FOREIGN KEY (event_id)    REFERENCES notification_event(event_id) ON DELETE SET NULL,
  CONSTRAINT fk_notification_template
    FOREIGN KEY (template_id) REFERENCES notification_template(template_id) ON DELETE SET NULL
);

CREATE INDEX idx_notification_user    ON notification(user_id);
CREATE INDEX idx_notification_status  ON notification(status);
CREATE INDEX idx_notification_created ON notification(created_at DESC);
CREATE INDEX idx_notification_event   ON notification(event_id);
CREATE INDEX idx_notification_template ON notification(template_id);

-- ===== TABLE: notification_log =====
CREATE TABLE notification_log (
  log_id               INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  action               VARCHAR(50) NOT NULL,
  logged_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  metadata             JSONB,
  notification_id      INT NOT NULL,
  CONSTRAINT fk_log_notification
    FOREIGN KEY (notification_id) REFERENCES notification(notification_id) ON DELETE CASCADE
);

CREATE INDEX idx_log_notification ON notification_log(notification_id);
CREATE INDEX idx_log_logged_at   ON notification_log(logged_at DESC);
CREATE INDEX idx_log_action     ON notification_log(action);

-- ===== TABLE: notification_channel =====
CREATE TABLE notification_channel (
  channel_id           INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  channel_type         VARCHAR(16) NOT NULL,
  channel_status       VARCHAR(16) NOT NULL,
  retry_count          INT NOT NULL DEFAULT 0,
  last_attempt_at      TIMESTAMPTZ,
  next_retry_at        TIMESTAMPTZ,
  error_code           VARCHAR(64),
  error_message        TEXT,
  created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  notification_id      INT NOT NULL,
  CONSTRAINT fk_channel_notification
    FOREIGN KEY (notification_id) REFERENCES notification(notification_id) ON DELETE CASCADE
);

CREATE INDEX idx_channel_notification ON notification_channel(notification_id);
CREATE INDEX idx_channel_status       ON notification_channel(channel_status);
CREATE INDEX idx_channel_type         ON notification_channel(channel_type);
CREATE INDEX idx_channel_next_retry   ON notification_channel(next_retry_at) WHERE next_retry_at IS NOT NULL;

-- ===== FUNCTION updated_at =====
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS trigger AS $$
BEGIN
  NEW.updated_at := NOW();
  RETURN NEW;
END$$ LANGUAGE plpgsql;

-- ===== TRIGGERS =====
CREATE TRIGGER trg_template_updated_at
BEFORE UPDATE ON notification_template
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_preference_updated_at
BEFORE UPDATE ON user_notification_preference
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

