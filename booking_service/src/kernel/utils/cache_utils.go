package utils

import "fmt"

var (
	CachePrefixAccommodation = "accommodation::%d"
)

func BuildCacheKeyGetAccommodation(ID int64) string {
	return fmt.Sprintf(CachePrefixAccommodation, ID)
}
