package specification

import "booking_service/core/domain/dto/request"

func ToGetBookingSpecification(params *request.BookingParams) (string, []any) {
	query := "SELECT * FROM bookings WHERE 1 = 1"
	args := make([]any, 0)

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

	if params.OrderBy != nil {
		query += " ORDER BY " + *params.OrderBy
	} else {
		query += " ORDER BY created_at"
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
	query := "SELECT COUNT(*) FROM bookings WHERE 1 = 1"
	args := make([]any, 0)

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
