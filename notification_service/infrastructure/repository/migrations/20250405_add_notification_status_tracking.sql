-- Add new columns for notification status tracking and retry mechanism
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'PENDING';
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS retry_count INT NOT NULL DEFAULT 0;
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS sent_at BIGINT NULL;
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS last_retry_at BIGINT NULL;

-- Create index for faster queries
CREATE INDEX IF NOT EXISTS idx_notifications_status ON notifications(status);
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_type ON notifications(type);