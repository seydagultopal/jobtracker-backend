ALTER TABLE users
    ADD COLUMN career_goal VARCHAR(80),
    ADD COLUMN onboarding_completed BOOLEAN NOT NULL DEFAULT TRUE;

-- Google accounts created before onboarding was introduced should complete it once.
UPDATE users
SET onboarding_completed = FALSE
WHERE auth_provider = 'GOOGLE'
  AND title IS NULL;
