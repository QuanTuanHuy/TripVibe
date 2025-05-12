package utils

import "time"

func EpochSecondToDay(epochSecond int64) int {
	t := time.Unix(epochSecond, 0)
	// Tạo thời điểm gốc (Epoch: 1/1/1970)
	epoch := time.Unix(0, 0)
	daysPassed := int(t.Sub(epoch).Hours() / 24)
	return daysPassed
}
