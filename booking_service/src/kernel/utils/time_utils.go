package utils

import "time"

func EpochMilliToDay(epochMilli int64) int {
	t := time.Unix(epochMilli, 0)
	// Tạo thời điểm gốc (Epoch: 1/1/1970)
	epoch := time.Unix(0, 0)
	daysPassed := int(t.Sub(epoch).Hours() / 24)
	return daysPassed
}
