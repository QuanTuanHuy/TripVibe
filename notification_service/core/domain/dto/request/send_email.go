package request

// SendEmailRequest defines the content of an email to send
type SendEmailRequest struct {
	Sender      EmailInfo   `json:"sender"`
	To          []EmailInfo `json:"to"`
	Subject     string      `json:"subject"`
	HtmlContent string      `json:"htmlContent"`
}

type EmailInfo struct {
	Name  string `json:"name"`
	Email string `json:"email"`
}
