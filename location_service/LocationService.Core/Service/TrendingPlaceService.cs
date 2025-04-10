using LocationService.Core.Domain.Dto.Response;
using LocationService.Core.Domain.Entity;
using LocationService.Core.UseCase;

namespace LocationService.Core.Service
{
    public interface ITrendingPlaceService
    {
        Task<TrendingPlaceEntity> CreateTrendingPlace(TrendingPlaceEntity trendingPlace);
        Task<TrendingPlaceEntity> GetTrendingPlaceById(long id);
        Task<PagedResponse<TrendingPlaceEntity>> GetTrendingPlacesByType(string type, int page, int pageSize);
        Task<PagedResponse<TrendingPlaceEntity>> GetTrendingPlaces(int page, int pageSize);
    }

    public class TrendingPlaceService : ITrendingPlaceService
    {
        private readonly IGetTrendingPlaceUseCase _getTrendingPlaceUseCase;
        private readonly ICreateTrendingPlaceUseCase _createTrendingPlaceUseCase;

        public TrendingPlaceService(IGetTrendingPlaceUseCase getTrendingPlaceUseCase,
            ICreateTrendingPlaceUseCase createTrendingPlaceUseCase)
        {
            _getTrendingPlaceUseCase = getTrendingPlaceUseCase;
            _createTrendingPlaceUseCase = createTrendingPlaceUseCase;
        }

        public async Task<TrendingPlaceEntity> CreateTrendingPlace(TrendingPlaceEntity trendingPlace)
        {
            return await _createTrendingPlaceUseCase.CreateTrendingPlace(trendingPlace);
        }

        public async Task<TrendingPlaceEntity> GetTrendingPlaceById(long id)
        {
            return await _getTrendingPlaceUseCase.GetTrendingPlaceById(id);
        }

        public async Task<PagedResponse<TrendingPlaceEntity>> GetTrendingPlacesByType(string type, int page, int pageSize)
        {
            return await _getTrendingPlaceUseCase.GetTrendingPlacesByType(type, page, pageSize);
        }

        public async Task<PagedResponse<TrendingPlaceEntity>> GetTrendingPlaces(int page, int pageSize)
        {
            return await _getTrendingPlaceUseCase.GetTrendingPlaces(page, pageSize);
        }
    }
}