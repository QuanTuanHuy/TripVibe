namespace PromotionService.Core.UseCase
{
    using System.Threading.Tasks;
    using PromotionService.Core.Domain.Dto.Request;
    using PromotionService.Core.Domain.Entity;

    public interface IUpdatePromotionUseCase
    {
        Task StopPromotionAsync(long userId, long id);
        Task<PromotionEntity> UpdatePromotionAsync(long userId, long id, UpdatePromotionDto req);
    }
}