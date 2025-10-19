CREATE TABLE works (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL
);

CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    role_id INTEGER NOT NULL REFERENCES roles(id),
    work_id INTEGER NOT NULL REFERENCES works(id),
    daily_value DECIMAL(10, 2) NOT NULL
);

CREATE TABLE day_adjustments (
    id SERIAL PRIMARY KEY,
    employee_id INTEGER NOT NULL REFERENCES employees(id),
    date VARCHAR(10) NOT NULL,
    adjustment_value DECIMAL(5, 2) NOT NULL,
    notes TEXT
);

CREATE TABLE monthly_payrolls (
    id SERIAL PRIMARY KEY,
    employee_id INTEGER NOT NULL REFERENCES employees(id),
    period_start_date VARCHAR(10) NOT NULL,
    period_end_date VARCHAR(10) NOT NULL,
    base_workdays DECIMAL(5, 2) NOT NULL,
    final_worked_days DECIMAL(5, 2) NOT NULL,
    total_payment DECIMAL(10, 2) NOT NULL,
    closed_at BIGINT
);
