package entity

type MemoRelationType string

const (
	MemoRelationReference MemoRelationType = "REFERENCE"
	MemoRelationComment   MemoRelationType = "COMMENT"
)

type MemoRelation struct {
	BaseEntity
	MemoID        int64            `json:"memoId"`
	RelatedMemoID int64            `json:"relatedMemoId"`
	Type          MemoRelationType `json:"type"`
}
