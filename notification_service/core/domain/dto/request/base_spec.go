package request

type BaseSpec struct {
	PageSize *int
	Page     *int
	OrderBy  *string
	Direct   *string
}
