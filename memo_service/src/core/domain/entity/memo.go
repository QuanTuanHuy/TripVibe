package entity

type Visibility string

const (
	Public  Visibility = "PUBLIC"
	Private Visibility = "PRIVATE"
)

type Memo struct {
	BaseEntity
	CreatorID  int64      `json:"creatorId"`
	Content    string     `json:"content"`
	Visibility Visibility `json:"visibility"`
	RowStatus  RowStatus  `json:"rowStatus"`
	Pinned     bool       `json:"pinned"`
	ParentID   *int64     `json:"parentId,omitempty"`
}
