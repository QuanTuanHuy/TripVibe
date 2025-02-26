package specification

import (
	"notification_service/core/domain/dto/request"
)

func ToGetNotificationSpecification(params *request.NotificationParams) (string, []interface{}) {
	query := "SELECT * FROM notifications WHERE 1 = 1"
	args := make([]interface{}, 0)

	if params.UserID != nil {
		query += " AND receiver_id = ?"
		args = append(args, *params.UserID)
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

	return query, args
}

func ToCountNotificationSpecification(params *request.NotificationParams) (string, []interface{}) {
	query := "SELECT COUNT(*) FROM notifications WHERE 1 = 1"
	var args []interface{}

	if params.UserID != nil {
		query += " AND receiver_id = ?"
		args = append(args, *params.UserID)
	}

	return query, args
}
