package huy.project.profile_service.core.service;

import huy.project.profile_service.core.domain.dto.request.CreateCreditCardDto;
import huy.project.profile_service.core.domain.dto.request.UpdateTouristDto;
import huy.project.profile_service.core.domain.entity.CreditCardEntity;
import huy.project.profile_service.core.domain.entity.TouristEntity;

public interface ITouristService {
    TouristEntity createTourist(Long userId, String email);
    TouristEntity updateTourist(Long id, UpdateTouristDto req);
    CreditCardEntity addCreditCardToTourist(Long touristId, CreateCreditCardDto req);
    TouristEntity getDetailTourist(Long id);
}
