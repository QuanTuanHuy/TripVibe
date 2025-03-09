package main

import (
	"booking_service/ui/bootstrap"
	"go.uber.org/fx"
)

func main() {
	// migrate database
	bootstrap.RunDatabase()

	// kafka
	bootstrap.RunKafkaConsumer()

	fx.New(bootstrap.All()).Run()
}
