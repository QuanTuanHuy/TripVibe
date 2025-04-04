package response

type ApiResponse struct {
	Data interface{} `json:"data"`
	Meta interface{} `json:"meta"`
}

type ApiMeta struct {
	Code    int    `json:"code"`
	Message string `json:"message"`
}
