package request

type BaseParams struct {
	PageSize *int    `json:"pageSize" form:"pageSize"`
	Page     *int    `json:"page" form:"page"`
	OrderBy  *string `json:"orderBy" form:"orderBy"`
	Direct   *string `json:"direct" form:"direct"`
}
