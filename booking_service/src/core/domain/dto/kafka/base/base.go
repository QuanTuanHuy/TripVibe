package basedto

type KafkaBaseDto struct {
	Cmd          string `json:"cmd"`
	ErrorCode    string `json:"errorCode"`
	ErrorMessage string `json:"errorMessage"`
	Timestamp    int64  `json:"timestamp"`
	Data         any    `json:"data"`
}

type DomainEventDto struct {
	EventID   string `json:"eventId"`
	EventType string `json:"eventType"`
	Timestamp int64  `json:"timestamp"`
	Data      any    `json:"data"`
}
