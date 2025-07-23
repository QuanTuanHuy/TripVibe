package request

type CreateMemoDto struct {
	Content    string `json:"content" validate:"required"`
	Visibility string `json:"visibility" validate:"required,oneof=PUBLIC PRIVATE"`
}
