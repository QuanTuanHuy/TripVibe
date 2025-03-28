package response

import "chat_service/core/domain/entity"

type GetChatRoomResponse struct {
	ChatRooms    []*entity.ChatRoomEntity `json:"chatRooms"`
	TotalItems   int64                    `json:"totalItems"`
	TotalPages   int64                    `json:"totalPages"`
	CurrentPage  int64                    `json:"currentPage"`
	PageSize     int64                    `json:"pageSize"`
	PreviousPage *int64                   `json:"previousPage"`
	NextPage     *int64                   `json:"nextPage"`
}

func ToGetChatRoomResponse(chatRooms []*entity.ChatRoomEntity, page, pageSize, totalPage, total int64, prevPage, nextPage *int64) *GetChatRoomResponse {
	return &GetChatRoomResponse{
		ChatRooms:    chatRooms,
		TotalItems:   total,
		TotalPages:   totalPage,
		CurrentPage:  page,
		PageSize:     pageSize,
		PreviousPage: prevPage,
		NextPage:     nextPage,
	}

}
