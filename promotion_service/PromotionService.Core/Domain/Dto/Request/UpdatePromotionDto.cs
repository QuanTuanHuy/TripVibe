using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Domain.Dto.Request
{
    public class UpdatePromotionDto
    {
        public string Name { get; set; }
        public string Description { get; set; }
        public string DiscountType { get; set; }
        public long DiscountValue { get; set; }
        public long StartDate { get; set; }
        public long EndDate { get; set; }

        public List<CreatePromotionConditionDto> Conditions { get; set; }
        public List<CreatePromotionUnitDto> Units { get; set; }

        public PromotionEntity ToEntity(PromotionEntity existedPromotion)
        {
            existedPromotion.Name = Name;
            existedPromotion.Description = Description;
            existedPromotion.DiscountType = DiscountType;
            existedPromotion.DiscountValue = DiscountValue;
            existedPromotion.StartDate = StartDate;
            existedPromotion.EndDate = EndDate;

            return existedPromotion;
        }
    }
}