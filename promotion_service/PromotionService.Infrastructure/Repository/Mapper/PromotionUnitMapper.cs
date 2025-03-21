namespace PromotionService.Infrastructure.Repository.Mapper
{
    using PromotionService.Core.Domain.Entity;
    using PromotionService.Infrastructure.Repository.Model;

    public class PromotionUnitMapper
    {
        public static PromotionUnitEntity ToEntity(PromotionUnitModel model)
        {
            return new PromotionUnitEntity
            {
                Id = model.Id,
                PromotionId = model.PromotionId,
                UnitId = model.UnitId,
                UnitName = model.UnitName
            };
        }

        public static PromotionUnitModel MapToModel(PromotionUnitEntity entity)
        {
            return new PromotionUnitModel
            {
                Id = entity.Id,
                PromotionId = entity.PromotionId,
                UnitId = entity.UnitId,
                UnitName = entity.UnitName
            };
        }
    }
}