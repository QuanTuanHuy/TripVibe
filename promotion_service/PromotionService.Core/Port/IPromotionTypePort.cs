using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;

namespace PromotionService.Core.Port;

public interface IPromotionTypePort
{
    Task<PromotionTypeEntity> AddPromotionTypeAsync(PromotionTypeEntity promotionType);
    Task<PromotionTypeEntity> GetPromotionTypeByNameAsync(string name);
    Task<PromotionTypeEntity> UpdatePromotionTypeAsync(long id, PromotionTypeEntity promotionType);
    Task<List<PromotionTypeEntity>> GetPromotionTypesAsync(PromotionTypeParams queryParams);
    Task<(List<PromotionTypeEntity> Items, int TotalCount)> GetPromotionTypesWithCountAsync(PromotionTypeParams queryParams);

    Task<PromotionTypeEntity> GetPromotionTypeByIdAsync(long id);
}