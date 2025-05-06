package specification

import (
	"booking_service/core/domain/dto/request"
	"time"
)

func ToGetBookingSpecification(params *request.BookingParams) (string, []any) {
	query := "SELECT * FROM bookings b"
	args := make([]any, 0)

	if params.UnitID != nil {
		query += " LEFT JOIN bookings_units bu ON b.id = bu.booking_id"
	}
	query += " WHERE 1 = 1"

	if params.UnitID != nil {
		query += " AND bu.unit_id = ?"
		args = append(args, *params.UnitID)
	}

	if params.UserID != nil {
		query += " AND tourist_id = ?"
		args = append(args, *params.UserID)
	}
	if params.Status != nil {
		query += " AND status = ?"
		args = append(args, *params.Status)
	}
	if params.AccommodationID != nil {
		query += " AND accommodation_id = ?"
		args = append(args, *params.AccommodationID)
	}
	if params.StartTime != nil && params.EndTime != nil && params.DateType != nil {
		switch *params.DateType {
		case "stay_to":
			query += " AND stay_to BETWEEN ? AND ?"
		case "created_at":
			query += " AND created_at BETWEEN ? AND ?"
		default:
			query += " AND stay_from BETWEEN ? AND ?"
		}
		args = append(args, time.Unix(*params.StartTime, 0), time.Unix(*params.EndTime, 0))
	}

	if params.OrderBy != nil {
		query += " ORDER BY " + *params.OrderBy
	} else {
		query += " ORDER BY b.created_at"
	}

	if params.Direct != nil {
		query += " " + *params.Direct
	} else {
		query += " DESC"
	}

	if params.Page != nil && params.PageSize != nil {
		offset := (*params.Page) * *params.PageSize
		query += " LIMIT ? OFFSET ?"
		args = append(args, *params.PageSize, offset)
	}

	return query, args
}

func ToCountBookingSpecification(params *request.BookingParams) (string, []any) {
	query := "SELECT COUNT(*) FROM bookings b"
	args := make([]any, 0)

	if params.UnitID != nil {
		query += " LEFT JOIN bookings_units bu ON b.id = bu.booking_id"
	}
	query += " WHERE 1 = 1"

	if params.UnitID != nil {
		query += " AND bu.unit_id = ?"
		args = append(args, *params.UnitID)
	}

	if params.UserID != nil {
		query += " AND tourist_id = ?"
		args = append(args, *params.UserID)
	}
	if params.Status != nil {
		query += " AND status = ?"
		args = append(args, *params.Status)
	}
	if params.AccommodationID != nil {
		query += " AND accommodation_id = ?"
		args = append(args, *params.AccommodationID)
	}

	return query, args
}
