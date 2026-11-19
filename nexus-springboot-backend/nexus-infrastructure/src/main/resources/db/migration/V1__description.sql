CREATE TABLE tickets(
        id          UUID    PRIMARY KEY,
        title      VARCHAR(255)    NOT NULL,
        description TEXT            NOT NULL,
        status      VARCHAR(20)     NOT NULL,
        priority    VARCHAR(20)     NOT NULL,
        created_at   TIMESTAMP WITH TIME ZONE NOT NULL
    );

CREATE INDEX idx_tickets_status ON tickets (status);
CREATE INDEX idx_tickets_priority ON tickets (priority);