package properties

import "github.com/golibs-starter/golib/config"

type InternalServiceProperties struct {
	Services []ServiceProperties `mapstructure:"internal"`
}

type ServiceProperties struct {
	Name        string
	Uri         string
	ContextPath string
}

func (i InternalServiceProperties) Prefix() string {
	return "app.services"
}

func NewInternalServiceProperties(loader config.Loader) (*InternalServiceProperties, error) {
	var properties InternalServiceProperties
	if err := loader.Bind(&properties); err != nil {
		return nil, err
	}
	return &properties, nil
}
