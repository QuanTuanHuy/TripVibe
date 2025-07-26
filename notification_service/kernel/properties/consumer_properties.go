package properties

import "github.com/golibs-starter/golib/config"

type EmailConsumerProperties struct {
	GroupId string
}

func (e EmailConsumerProperties) Prefix() string {
	return "app.kafka.consumer"
}

func NewEmailConsumerProperties(loader config.Loader) (*EmailConsumerProperties, error) {
	var properties EmailConsumerProperties
	if err := loader.Bind(&properties); err != nil {
		return nil, err
	}
	return &properties, nil
}
