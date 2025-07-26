package properties

import "github.com/golibs-starter/golib/config"

type KafkaProperties struct {
	BootstrapServers []string
}

func (k KafkaProperties) Prefix() string {
	return "app.kafka"
}

func NewKafkaProperties(loader config.Loader) (*KafkaProperties, error) {
	var properties KafkaProperties
	if err := loader.Bind(&properties); err != nil {
		return nil, err
	}
	return &properties, nil
}
