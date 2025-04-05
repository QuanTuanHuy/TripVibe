using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Domain.Dto.Request;

public class CreatePromotionTypeDto
{
    public string Name { get; set; }
    public string Description { get; set; }

    public PromotionTypeEntity toEntity()
    {
        return new PromotionTypeEntity
        {
            Name = Name,
            Description = Description
        };
    }
}