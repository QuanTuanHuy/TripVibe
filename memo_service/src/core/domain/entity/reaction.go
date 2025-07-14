package entity

type Reaction struct {
	BaseEntity
	CreatorID    int64  `json:"creatorId"`
	ContentID    int64  `json:"contentId"`
	ReactionType string `json:"reactionType"`
}
