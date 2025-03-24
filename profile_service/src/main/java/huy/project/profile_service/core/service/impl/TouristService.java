package huy.project.profile_service.core.service.impl;

import huy.project.profile_service.core.domain.dto.request.CreateCreditCardDto;
import huy.project.profile_service.core.domain.dto.request.UpdateTouristDto;
import huy.project.profile_service.core.domain.entity.CreditCardEntity;
import huy.project.profile_service.core.domain.entity.TouristEntity;
import huy.project.profile_service.core.service.ITouristService;
import huy.project.profile_service.core.usecase.CreateTouristUseCase;
import huy.project.profile_service.core.usecase.GetTouristUseCase;
import huy.project.profile_service.core.usecase.UpdateTouristUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TouristService implements ITouristService {
    private final CreateTouristUseCase createTouristUseCase;
    private final UpdateTouristUseCase updateTouristUseCase;
    private final GetTouristUseCase getTouristUseCase;

    @Override
    public TouristEntity createTourist(Long userId, String email) {
        return createTouristUseCase.createTourist(userId, email);
    }

    @Override
    public TouristEntity updateTourist(Long id, UpdateTouristDto req) {
        return updateTouristUseCase.updateTourist(id, req);
    }

    @Override
    public CreditCardEntity addCreditCardToTourist(Long touristId, CreateCreditCardDto req) {
        return updateTouristUseCase.addCreditCardToTourist(touristId, req);
    }

    @Override
    public TouristEntity getDetailTourist(Long id) {
        return getTouristUseCase.getDetailTourist(id);
    }

    @Override
    public List<TouristEntity> getTouristsByIds(List<Long> touristIds) {
        return getTouristUseCase.getTouristsByIds(touristIds);
    }
}
