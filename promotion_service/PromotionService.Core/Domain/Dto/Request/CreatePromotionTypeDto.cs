using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Domain.Dto.Request;

public class CreatePromotionTypeDto
{
    public string name { get; set; }
    public string description { get; set; }

    public PromotionTypeEntity toEntity()
    {
        return new PromotionTypeEntity
        {
            Name = name,
            Description = description
        };
    }
}