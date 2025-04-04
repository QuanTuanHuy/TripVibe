package specification

import (
	"notification_service/core/domain/dto/request"
	"notification_service/core/domain/entity"
)

func ToGetNotificationSpecification(params *request.NotificationParams) (string, []interface{}) {
	query := "SELECT * FROM notifications WHERE 1 = 1"
	args := make([]interface{}, 0)

	if params.UserID != nil {
		query += " AND user_id = ?"
		args = append(args, *params.UserID)
	}
	if params.Status != nil {
		query += " AND status = ?"
		args = append(args, entity.NotificationStatus(*params.Status))
	}
	if params.Type != nil {
		query += " AND type = ?"
		args = append(args, entity.NotificationType(*params.Type))
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

func ToCountNotificationSpecification(params *request.NotificationParams) (string, []interface{}) {
	query := "SELECT COUNT(*) FROM notifications WHERE 1 = 1"
	args := make([]interface{}, 0)

	if params.UserID != nil {
		query += " AND user_id = ?"
		args = append(args, *params.UserID)
	}
	if params.Status != nil {
		query += " AND status = ?"
		args = append(args, entity.NotificationStatus(*params.Status))
	}
	if params.Type != nil {
		query += " AND type = ?"
		args = append(args, entity.NotificationType(*params.Type))
	}

	return query, args
}
