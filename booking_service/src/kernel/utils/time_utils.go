package utils

import "time"

func EpochSecondToDay(epochSecond int64) int {
	t := time.Unix(epochSecond, 0)
	epoch := time.Unix(0, 0)
	daysPassed := int(t.Sub(epoch).Hours() / 24)
	return daysPassed
}

func EpochSecondToDate(epochSecond int64) string {
	t := time.Unix(epochSecond, 0)
	return t.Format("2006-01-02")
}
