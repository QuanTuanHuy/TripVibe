package main

import (
	"fmt"

	"github.com/quantuanhuy/lib/src/ui/bootstrap"
	"go.uber.org/fx"
)

func main() {
	fmt.Println("Hello, World!")
	fx.New(bootstrap.All()).Run()
}
