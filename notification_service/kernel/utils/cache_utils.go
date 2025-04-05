package utils

import "fmt"

var (
	CachePrefixUserProfile = "notification_service.user_profile::%d"
)

func BuildCacheKeyGetUserProfileByID(ID int64) string {
	return fmt.Sprintf(CachePrefixUserProfile, ID)
}
