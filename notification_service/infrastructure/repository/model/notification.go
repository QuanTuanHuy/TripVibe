package model

// Notification represents a notification in the database
type Notification struct {
	ID          int64                  `gorm:"primaryKey;column:id;autoIncrement"`
	Type        string                 `gorm:"column:type;not null;type:varchar(20)"`
	Title       string                 `gorm:"column:title;not null;type:varchar(255)"`
	Content     string                 `gorm:"column:content;not null;type:text"`
	UserID      int64                  `gorm:"column:user_id;not null"`
	Recipient   string                 `gorm:"column:recipient;not null;type:varchar(255)"`
	Metadata    map[string]interface{} `gorm:"column:metadata;type:jsonb"`
	Status      string                 `gorm:"column:status;not null;type:varchar(20);default:'PENDING'"`
	RetryCount  int                    `gorm:"column:retry_count;type:int;default:0"`
	SentAt      *int64                 `gorm:"column:sent_at;type:bigint"`
	LastRetryAt *int64                 `gorm:"column:last_retry_at;type:bigint"`
	CreatedAt   int64                  `gorm:"column:created_at;not null;type:bigint"`
	UpdatedAt   int64                  `gorm:"column:updated_at;not null;type:bigint"`
}

// TableName specifies the table name for the Notification model
func (n Notification) TableName() string {
	return "notifications"
}
