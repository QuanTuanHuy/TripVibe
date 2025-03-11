package basedto

type KafkaBaseDto struct {
	Cmd          string `json:"cmd"`
	ErrorCode    string `json:"errorCode"`
	ErrorMessage string `json:"errorMessage"`
	Timestamp    int64  `json:"timestamp"`
	Data         any    `json:"data"`
}
