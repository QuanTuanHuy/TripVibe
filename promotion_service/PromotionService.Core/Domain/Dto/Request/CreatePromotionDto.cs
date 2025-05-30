﻿using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Domain.Dto.Request
{
    public class CreatePromotionDto
    {
        public long CreatedBy { get; set; }
        public long TypeId { get; set; }
        public long AccommodationId { get; set; }
        public string Name { get; set; }
        public string Description { get; set; }
        public bool IsActive { get; set; }
        public string DiscountType { get; set; }
        public long DiscountValue { get; set; }
        public long UsageLimit { get; set; }
        public long StartDate { get; set; }
        public long EndDate { get; set; }

        public List<CreatePromotionConditionDto> Conditions { get; set; }
        public List<CreatePromotionUnitDto> Units { get; set; }

        public PromotionEntity ToEntity()
        {
            return new PromotionEntity
            {
                CreatedBy = CreatedBy,
                TypeId = TypeId,
                AccommodationId = AccommodationId,
                Name = Name,
                Description = Description,
                IsActive = IsActive,
                DiscountType = DiscountType,
                DiscountValue = DiscountValue,
                UsageLimit = UsageLimit,
                StartDate = StartDate,
                EndDate = EndDate
            };
        }
    }

    public class CreatePromotionConditionDto
    {
        public long ConditionId { get; set; }
        public int ConditionValue { get; set; }

        public PromotionConditionEntity ToEntity(long promotionId)
        {
            return new PromotionConditionEntity
            {
                PromotionId = promotionId,
                ConditionId = ConditionId,
                ConditionValue = ConditionValue
            };
        }
    }

    public class CreatePromotionUnitDto
    {
        public long UnitId { get; set; }
        public string UnitName { get; set; }

        public PromotionUnitEntity ToEntity(long promotionId)
        {
            return new PromotionUnitEntity
            {
                PromotionId = promotionId,
                UnitId = UnitId,
                UnitName = UnitName
            };
        }
    }
}