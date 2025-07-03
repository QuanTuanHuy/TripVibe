package config

import "go.uber.org/zap"

func NewLogger() *zap.Logger {
	logger, err := zap.NewDevelopment()
	if err != nil {
		panic("Failed to create logger: " + err.Error())
	}

	zap.ReplaceGlobals(logger)

	return logger
}
