using PromotionService.Core.Domain.Entity;
using PromotionService.Infrastructure.Repository.Model;

namespace PromotionService.Infrastructure.Repository.Mapper;

public class PromotionTypeMapper
{
    public static PromotionTypeEntity ToEntity(PromotionTypeModel? promotionType)
    {
        if (promotionType == null)
            return null;
            
        return new PromotionTypeEntity
        {
            Id = promotionType.Id,
            Name = promotionType.Name,
            Description = promotionType.Description
        };
    }
    
    public static PromotionTypeModel ToModel(PromotionTypeEntity? promotionType)
    {
        if (promotionType == null)
            return null;
            
        return new PromotionTypeModel
        {
            Id = promotionType.Id,
            Name = promotionType.Name,
            Description = promotionType.Description
        };
    }
}