using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Domain.Mapper;

public class PromotionTypeMapper
{
    public static PromotionTypeEntity ToEntity(UpdatePromotionTypeDto req)
    {
        return new PromotionTypeEntity
        {
            Name = req.Name,
            Description = req.Description
        };
    }
}