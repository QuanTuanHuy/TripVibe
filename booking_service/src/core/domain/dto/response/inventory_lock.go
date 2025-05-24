package response

type InventoryLockResponse struct {
	LockID    string   `json:"lockId"`
	Success   bool     `json:"success"`
	Errors    []string `json:"errors"`
	ExpiresAt int64    `json:"expiresAt"`
}
