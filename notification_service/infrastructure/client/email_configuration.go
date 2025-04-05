package client

// EmailConfiguration defines settings for email sending
type EmailConfiguration struct {
	SMTPHost     string `mapstructure:"smtp-host"`
	SMTPPort     string `mapstructure:"smtp-port"`
	SMTPUsername string `mapstructure:"smtp-username"`
	SMTPPassword string `mapstructure:"smtp-password"`
	FromEmail    string `mapstructure:"from-email"`
}

// NewEmailConfiguration creates a default email configuration
func NewEmailConfiguration() *EmailConfiguration {
	return &EmailConfiguration{
		SMTPHost:     "smtp.example.com",
		SMTPPort:     "587",
		SMTPUsername: "notification@example.com",
		SMTPPassword: "your-password",
		FromEmail:    "notifications@example.com",
	}
}
