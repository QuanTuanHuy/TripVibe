package entity

type RowStatus string

const (
	Normal   RowStatus = "NORMAL"
	Archived RowStatus = "ARCHIVED"
)

func (r RowStatus) String() string {
	return string(r)
}
