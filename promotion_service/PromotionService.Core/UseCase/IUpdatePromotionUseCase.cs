namespace PromotionService.Core.UseCase
{
    using System.Threading.Tasks;

    public interface IUpdatePromotionUseCase
    {
        Task StopPromotionAsync(long userId, long id);
    }
}