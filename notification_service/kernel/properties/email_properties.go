package properties

import (
	"github.com/golibs-starter/golib/config"
)

type EmailProperties struct {
	ApiKey string
	Url    string
}

func (e EmailProperties) Prefix() string {
	return "app.services.email"
}

func NewEmailProperties(loader config.Loader) (*EmailProperties, error) {
	var properties EmailProperties
	if err := loader.Bind(&properties); err != nil {
		return nil, err
	}
	return &properties, nil
}
