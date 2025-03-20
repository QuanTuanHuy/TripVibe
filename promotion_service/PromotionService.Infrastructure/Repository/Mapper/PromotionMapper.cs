using PromotionService.Core.Domain.Entity;
using PromotionService.Infrastructure.Repository.Model;

namespace PromotionService.Infrastructure.Repository.Mapper;

public class PromotionMapper
{
    public static PromotionEntity ToEntity(PromotionModel? promotion)
    {
        if (promotion == null)
        {
            return null;
        }

        return new PromotionEntity
        {
            Id = promotion.Id,
            CreatedBy = promotion.CreatedBy,
            TypeId = promotion.TypeId,
            AccommodationId = promotion.AccommodationId,
            Name = promotion.Name,
            Description = promotion.Description,
            IsActive = promotion.IsActive,
            DiscountType = promotion.DiscountType,
            DiscountValue = promotion.DiscountValue,
            UsageLimit = promotion.UsageLimit,
            UsageCount = promotion.UsageCount,
            StartDate = ((DateTimeOffset)promotion.StartDate).ToUnixTimeMilliseconds(),
            EndDate = ((DateTimeOffset)promotion.EndDate).ToUnixTimeMilliseconds()
        };
    }

    public static PromotionModel ToModel(PromotionEntity promotionEntity)
    {
        if (promotionEntity == null)
        {
            return null;
        }

        return new PromotionModel
        {
            Id = promotionEntity.Id,
            CreatedBy = promotionEntity.CreatedBy,
            TypeId = promotionEntity.TypeId,
            AccommodationId = promotionEntity.AccommodationId,
            Name = promotionEntity.Name,
            Description = promotionEntity.Description,
            IsActive = promotionEntity.IsActive,
            DiscountType = promotionEntity.DiscountType,
            DiscountValue = promotionEntity.DiscountValue,
            UsageLimit = promotionEntity.UsageLimit,
            UsageCount = promotionEntity.UsageCount,
            StartDate = DateTime.SpecifyKind(
            DateTimeOffset.FromUnixTimeMilliseconds(promotionEntity.StartDate).DateTime,
            DateTimeKind.Utc),
            EndDate = DateTime.SpecifyKind(
            DateTimeOffset.FromUnixTimeMilliseconds(promotionEntity.EndDate).DateTime,
            DateTimeKind.Utc)
        };
    }
}