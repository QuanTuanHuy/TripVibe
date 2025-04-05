package utils

func Unique(intSlice []int64) []int64 {
	keys := make(map[int64]bool)
	list := make([]int64, 0)

	for _, entry := range intSlice {
		if _, value := keys[entry]; !value {
			keys[entry] = true
			list = append(list, entry)
		}
	}

	return list
}
