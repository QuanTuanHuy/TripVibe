package specification

import (
	"chat_service/core/domain/dto/request"
)

func ToGetChatRoomSpecification(params *request.ChatRoomQueryParams) (string, []any) {
	var args []any
	query := "SELECT c.* FROM chat_rooms c LEFT JOIN room_participants rp1 ON c.id = rp1.chat_room_id"

	if params.ChatUserID != nil && *params.ChatUserID > 0 {
		query += " LEFT JOIN room_participants rp2 ON c.id = rp2.chat_room_id WHERE rp1.participant_id = ? AND rp2.participant_id = ?"
		args = append(args, *params.UserID, *params.ChatUserID)
	} else {
		query += " WHERE rp1.participant_id = ?"
		args = append(args, *params.UserID)
	}

	query += " ORDER BY c.last_message_id DESC"

	if params.Page != nil && params.PageSize != nil {
		offset := (*params.Page) * *params.PageSize
		query += " LIMIT ? OFFSET ?"
		args = append(args, *params.PageSize, offset)
	}

	return query, args
}

func ToCountChatRoomSpecification(params *request.ChatRoomQueryParams) (string, []any) {
	var args []any
	query := "SELECT COUNT(*) FROM chat_rooms c LEFT JOIN room_participants rp1 ON c.id = rp1.chat_room_id"

	if params.ChatUserID != nil && *params.ChatUserID > 0 {
		query += " LEFT JOIN room_participants rp2 ON c.id = rp2.chat_room_id WHERE rp1.participant_id = ? AND rp2.participant_id = ?"
		args = append(args, *params.UserID, *params.ChatUserID)
	} else {
		query += " WHERE rp1.participant_id = ?"
		args = append(args, *params.UserID)
	}

	return query, args
}
