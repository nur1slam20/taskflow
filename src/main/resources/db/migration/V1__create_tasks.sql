CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    description VARCHAR(2000),
    status VARCHAR(24) NOT NULL DEFAULT 'TODO',
    due_date DATE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_tasks_status_created_at ON tasks (status, created_at DESC);
