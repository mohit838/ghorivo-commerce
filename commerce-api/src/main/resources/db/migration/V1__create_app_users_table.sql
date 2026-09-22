CREATE TABLE app_users
(
    id            UUID         NOT NULL,
    full_name     VARCHAR(120) NOT NULL,
    email         VARCHAR(254) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    status        VARCHAR(20)  NOT NULL,
    version       BIGINT       NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_app_users
        PRIMARY KEY (id),

    CONSTRAINT uq_app_users_email
        UNIQUE (email),

    CONSTRAINT chk_app_users_full_name
        CHECK (btrim(full_name) <> ''),

    CONSTRAINT chk_app_users_email_normalized
        CHECK (email = lower(btrim(email))),

    CONSTRAINT chk_app_users_role
        CHECK (role IN ('ADMIN', 'STAFF')),

    CONSTRAINT chk_app_users_status
        CHECK (status IN ('ACTIVE', 'INACTIVE'))
);