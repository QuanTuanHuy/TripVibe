using PromotionService.Core.Domain.Dto;

namespace PromotionService.Core.Port
{
    public interface IAccommodationPort
    {
        Task<AccommodationDto?> GetAccommodationById(long accId);
    }
}