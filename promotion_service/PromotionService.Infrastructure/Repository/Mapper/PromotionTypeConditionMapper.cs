using PromotionService.Core.Domain.Entity;
using PromotionService.Infrastructure.Repository.Model;

namespace PromotionService.Infrastructure.Repository.Mapper
{
    public static class PromotionTypeConditionMapper
    {
        public static PromotionTypeConditionModel ToModel(PromotionTypeConditionEntity entity)
        {
            return new PromotionTypeConditionModel
            {
                Id = entity.Id,
                PromotionTypeId = entity.PromotionTypeId,
                ConditionId = entity.ConditionId,
                DefaultValue = entity.DefaultValue
            };
        }

        public static PromotionTypeConditionEntity ToEntity(PromotionTypeConditionModel model)
        {
            return new PromotionTypeConditionEntity
            {
                Id = model.Id,
                PromotionTypeId = model.PromotionTypeId,
                ConditionId = model.ConditionId,
                DefaultValue = model.DefaultValue
            };
        }
    }
}