-- Add plan_name and features to subscription_plan table

ALTER TABLE subscription_plan
ADD COLUMN plan_name VARCHAR(255),
ADD COLUMN features JSONB DEFAULT '[]'::jsonb;

-- Update existing records to use plan_code as plan_name if null
UPDATE subscription_plan
SET plan_name = plan_code
WHERE plan_name IS NULL;

-- Make plan_name NOT NULL after setting defaults
ALTER TABLE subscription_plan
ALTER COLUMN plan_name SET NOT NULL;

-- Add index for faster queries
CREATE INDEX idx_plan_name ON subscription_plan(plan_name);

-- Add comment for documentation
COMMENT ON COLUMN subscription_plan.plan_name IS 'User-friendly display name for the subscription plan';
COMMENT ON COLUMN subscription_plan.features IS 'JSON array of feature descriptions for the plan';
