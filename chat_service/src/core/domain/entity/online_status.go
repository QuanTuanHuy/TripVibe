package entity

type OnLineStatusEntity struct {
	BaseEntity
	IsOnline bool  `json:"isOnline"`
	LastSeen int64 `json:"lastSeen"`
}
