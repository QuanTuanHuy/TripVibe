namespace PromotionService.Core.UseCase
{
    public interface IDeletePromotionTypeUseCase
    {
        Task DeletePromotionTypeAsync(long id);
    }
}