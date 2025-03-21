package request

type DeleteUnitDto struct {
	AccommodationId int64 `json:"accommodationId"`
	UnitId          int64 `json:"unitId"`
}
