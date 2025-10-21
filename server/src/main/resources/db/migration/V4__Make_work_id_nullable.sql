-- Make work_id nullable to allow unassigned employees
ALTER TABLE employees ALTER COLUMN work_id DROP NOT NULL;
