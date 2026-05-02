CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    must_change_password BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by BIGINT,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by BIGINT
);

CREATE TABLE leads (
    id BIGSERIAL PRIMARY KEY,
    lead_name VARCHAR(150) NOT NULL,
    company_name VARCHAR(150) NOT NULL,
    phone VARCHAR(60),
    email VARCHAR(180),
    lead_source VARCHAR(120) NOT NULL,
    company_website VARCHAR(200),
    job_title VARCHAR(120),
    linkedin_url VARCHAR(200),
    notes TEXT,
    current_stage VARCHAR(30) NOT NULL,
    assigned_to_user_id BIGINT REFERENCES users(id),
    assigned_at TIMESTAMP WITH TIME ZONE,
    archived BOOLEAN NOT NULL DEFAULT FALSE,
    next_follow_up_date DATE,
    next_follow_up_status VARCHAR(20),
    last_activity_at DATE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by BIGINT REFERENCES users(id),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by BIGINT REFERENCES users(id)
);

CREATE TABLE lead_assignment_history (
    id BIGSERIAL PRIMARY KEY,
    lead_id BIGINT NOT NULL REFERENCES leads(id),
    previous_rep_id BIGINT REFERENCES users(id),
    new_rep_id BIGINT REFERENCES users(id),
    changed_by BIGINT NOT NULL REFERENCES users(id),
    note TEXT,
    changed_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE lead_stage_history (
    id BIGSERIAL PRIMARY KEY,
    lead_id BIGINT NOT NULL REFERENCES leads(id),
    previous_stage VARCHAR(30) NOT NULL,
    new_stage VARCHAR(30) NOT NULL,
    changed_by BIGINT NOT NULL REFERENCES users(id),
    changed_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE lead_activity (
    id BIGSERIAL PRIMARY KEY,
    lead_id BIGINT NOT NULL REFERENCES leads(id),
    activity_type VARCHAR(20) NOT NULL,
    activity_date DATE NOT NULL,
    notes TEXT,
    logged_by BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE lead_follow_up (
    id BIGSERIAL PRIMARY KEY,
    lead_id BIGINT NOT NULL REFERENCES leads(id),
    assigned_rep_id BIGINT REFERENCES users(id),
    due_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    scheduled_by BIGINT NOT NULL REFERENCES users(id),
    completed_by BIGINT REFERENCES users(id),
    completed_at TIMESTAMP WITH TIME ZONE,
    completion_note TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_leads_assigned_to_user_id ON leads (assigned_to_user_id);
CREATE INDEX idx_leads_current_stage ON leads (current_stage);
CREATE INDEX idx_leads_next_follow_up_date ON leads (next_follow_up_date);
CREATE INDEX idx_stage_history_lead_changed_at ON lead_stage_history (lead_id, changed_at DESC);
CREATE INDEX idx_activity_lead_activity_date ON lead_activity (lead_id, activity_date DESC);
CREATE INDEX idx_follow_up_assigned_due_status ON lead_follow_up (assigned_rep_id, due_date, status);
