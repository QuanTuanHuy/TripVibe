package utils

import "fmt"

var (
	CachePrefixAccommodation = "accommodation::%d"
	CachePrefixBooking       = "booking::%d"
	CachePrefixUser          = "user::%d"
)

func BuildCacheKeyGetAccommodation(ID int64) string {
	return fmt.Sprintf(CachePrefixAccommodation, ID)
}

func BuildCacheKeyGetBooking(ID int64) string {
	return fmt.Sprintf(CachePrefixBooking, ID)
}

func BuildCacheKeyGetUser(ID int64) string {
	return fmt.Sprintf(CachePrefixUser, ID)
}
