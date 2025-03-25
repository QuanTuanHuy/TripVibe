namespace PromotionService.Core.Domain.Dto.Request
{
    public class AddConditionToPromotionTypeDto
    {
        public List<AddConditionDto> Conditions { get; set; }
    }

    public class AddConditionDto 
    {
        public long ConditionId { get; set; }
        public long DefaultValue { get; set; }
    }
}