﻿using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Service;

public interface IPromotionTypeService
{
    Task<PromotionTypeEntity> CreatePromotionTypeAsync(PromotionTypeEntity promotionType);
    Task<(List<PromotionTypeEntity>, int)> GetPromotionTypesAsync(PromotionTypeParams queryParams);
    Task<PromotionTypeEntity> UpdatePromotionTypeAsync(long id, UpdatePromotionTypeDto req);
    Task DeletePromotionTypeAsync(long id);
    Task<List<PromotionTypeConditionEntity>> AddConditionsToPromotionTypeAsync(long promotionTypeId, AddConditionToPromotionTypeDto req);
    Task<PromotionTypeEntity> GetPromotionTypeByIdAsync(long id);
}