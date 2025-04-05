package usecase

import (
	"chat_service/core/domain/entity"
	"chat_service/core/port"
	"chat_service/kernel/utils"
	"context"
)

type IGetParticipantUseCase interface {
	GetParticipantsByRoomIDs(ctx context.Context, roomIDs []int64) (map[int64][]*entity.ParticipantEntity, error)
}

type GetParticipantUseCase struct {
	participantPort     port.IParticipantPort
	roomParticipantPort port.IRoomParticipantPort
}

func (g GetParticipantUseCase) GetParticipantsByRoomIDs(ctx context.Context, roomIDs []int64) (map[int64][]*entity.ParticipantEntity, error) {
	roomParticipants, err := g.roomParticipantPort.GetByRoomIDs(ctx, roomIDs)
	if err != nil {
		return nil, err
	}
	if len(roomParticipants) == 0 {
		return make(map[int64][]*entity.ParticipantEntity), nil
	}

	participantIDs := make([]int64, 0)
	for _, roomParticipant := range roomParticipants {
		participantIDs = append(participantIDs, roomParticipant.ParticipantID)
	}
	participantIDs = utils.Unique(participantIDs)

	participants, err := g.participantPort.GetParticipantsByIDs(ctx, participantIDs)
	if err != nil {
		return nil, err
	}

	participantsMap := make(map[int64]*entity.ParticipantEntity)
	for _, participant := range participants {
		participantsMap[participant.UserID] = participant
	}

	result := make(map[int64][]*entity.ParticipantEntity)
	for _, roomParticipant := range roomParticipants {
		if participant, exists := participantsMap[roomParticipant.ParticipantID]; exists {
			result[roomParticipant.ChatRoomID] = append(result[roomParticipant.ChatRoomID], participant)
		}
	}

	return result, nil
}
func NewGetParticipantUseCase(participantPort port.IParticipantPort,
	roomParticipantPort port.IRoomParticipantPort) IGetParticipantUseCase {
	return &GetParticipantUseCase{
		participantPort:     participantPort,
		roomParticipantPort: roomParticipantPort,
	}
}
