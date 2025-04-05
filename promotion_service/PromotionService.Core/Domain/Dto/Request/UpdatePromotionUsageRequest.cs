namespace PromotionService.Core.Domain.Dto.Request
{
    using System.Collections.Generic;
    using System.ComponentModel.DataAnnotations;

    public class UpdatePromotionUsageRequest
    {
        [Required]
        public List<long> PromotionIds { get; set; } = new List<long>();
    }
}