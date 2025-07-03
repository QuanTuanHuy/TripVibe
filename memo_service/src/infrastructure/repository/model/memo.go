package model

type MemoModel struct {
	BaseModel
	CreatorID  int64  `gorm:"column:creator_id"`
	Content    string `gorm:"column:content"`
	Visibility string `gorm:"column:visibility"`
	RowStatus  string `gorm:"column:row_status"`
	Pinned     bool   `gorm:"column:pinned"`
	ParentID   *int64 `gorm:"column:parent_id"`
}

func (m MemoModel) TableName() string {
	return "memos"
}
