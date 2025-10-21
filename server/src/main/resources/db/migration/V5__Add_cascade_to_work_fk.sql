-- Drop the existing foreign key constraint
ALTER TABLE employees DROP CONSTRAINT employees_work_id_fkey;

-- Recreate it with ON DELETE SET NULL so work deletion automatically unassigns employees
ALTER TABLE employees
ADD CONSTRAINT employees_work_id_fkey
FOREIGN KEY (work_id) REFERENCES works(id)
ON DELETE SET NULL;
