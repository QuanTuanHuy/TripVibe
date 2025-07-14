package entity

type Attachment struct {
	BaseEntity
	CreatorID int64  `json:"creatorId"`
	FileName  string `json:"fileName"`
	FilePath  string `json:"filePath"`
	Type      string `json:"type"`
	Size      int64  `json:"size"`
	MemoID    int64  `json:"memoId"`
}
