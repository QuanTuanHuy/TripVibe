namespace LocationService.Core.UseCase
{
    using System.Threading.Tasks;
    using LocationService.Core.Domain.Constant;
    using LocationService.Core.Domain.Dto.Request;
    using LocationService.Core.Domain.Entity;
    using LocationService.Core.Exception;
    using LocationService.Core.Port;
    using LocationService.Core.Service;
    using LocationService.Kernel.Utils;

    public interface IGetAttractionUseCase
    {
        Task<AttractionEntity> GetDetailAttractionAsync(long id);
        Task<(List<AttractionEntity> attractions, long count)> GetAttractionsAsync(GetAttractionParams parameters);
    }

    public class GetAttractionUseCase : IGetAttractionUseCase
    {
        private readonly IAttractionPort _attractionPort;
        private readonly IAttractionSchedulePort _attractionSchedulePort;
        private readonly IAttractionLanguagePort _attractionLanguagePort;
        private readonly ICachePort _cachePort;
        private readonly IGetImageUseCase _getImageUseCase;
        private readonly IGetLocationUseCase _getLocationUseCase;
        private readonly IGetLanguageUseCase _getLanguageUseCase;
        private readonly IGetCategoryUseCase _getCategoryUseCase;

        public GetAttractionUseCase(
            IAttractionPort attractionPort,
            IAttractionSchedulePort attractionSchedulePort,
            IAttractionLanguagePort attractionLanguagePort,
            ICachePort cachePort,
            IGetImageUseCase getImageUseCase,
            IGetLocationUseCase getLocationUseCase,
            IGetLanguageUseCase getLanguageUseCase,
            IGetCategoryUseCase getCategoryUseCase)
        {
            _attractionPort = attractionPort;
            _attractionSchedulePort = attractionSchedulePort;
            _attractionLanguagePort = attractionLanguagePort;
            _cachePort = cachePort;
            _getImageUseCase = getImageUseCase;
            _getLocationUseCase = getLocationUseCase;
            _getLanguageUseCase = getLanguageUseCase;
            _getCategoryUseCase = getCategoryUseCase;
        }

        public async Task<AttractionEntity> GetDetailAttractionAsync(long id)
        {
            var cacheKey = CacheUtils.BuildCacheKeyGetAttractionById(id);
            var cachedAttraction = await _cachePort.GetFromCacheAsync<AttractionEntity>(cacheKey);
            if (cachedAttraction != null)
            {
                return cachedAttraction;
            }

            var attraction = await _attractionPort.GetAttractionByIdAsync(id);
            if (attraction == null)
            {
                throw new AppException(ErrorCode.ATTRACTION_NOT_FOUND);
            }

            attraction.Schedules = await _attractionSchedulePort.GetSchedulesByAttractionIdAsync(id);
            attraction.Images = await _getImageUseCase.GetImagesByEntityIdAndEntityType(id, ImageEntityType.Attraction);
            attraction.Location = await _getLocationUseCase.GetLocationByIdAsync(attraction.LocationId);
            attraction.Category = await _getCategoryUseCase.GetCategoryByIdAsync(attraction.CategoryId);

            var attractionLangs = await _attractionLanguagePort.GetByAttractionIdAsync(id);
            var languageIds = attractionLangs.Select(x => x.LanguageId).ToList();
            attraction.Languages = await _getLanguageUseCase.GetLanguagesByIdsAsync(languageIds);

            await _cachePort.SetToCacheAsync(cacheKey, attraction, CacheConstant.DEFAULT_TTL);

            return attraction;
        }

        public async Task<(List<AttractionEntity> attractions, long count)> GetAttractionsAsync(GetAttractionParams parameters)
        {
            var attractions = await _attractionPort.GetAttractionsAsync(parameters);
            var count = await _attractionPort.CountAttractionsAsync(parameters);

            var locationIds = attractions.Select(x => x.LocationId).Distinct().ToList();
            var locations = await _getLocationUseCase.GetLocationsByIdsAsync(locationIds);
            var locationMap = locations.ToDictionary(x => x.Id, x => x);

            var categoryIds = attractions.Select(x => x.CategoryId).Distinct().ToList();
            var categories = await _getCategoryUseCase.GetCategoriesByIdsAsync(categoryIds);
            var categoryMap = categories.ToDictionary(x => x.Id, x => x);

            var attractionIds = attractions.Select(x => x.Id).Distinct().ToList();
            var attractionLangs = await _attractionLanguagePort.GetByAttractionIds(attractionIds);
            var languageIds = attractionLangs.Select(x => x.LanguageId).Distinct().ToList();
            var languages = await _getLanguageUseCase.GetLanguagesByIdsAsync(languageIds);
            var languageMap = languages.ToDictionary(x => x.Id, x => x);

            foreach (var attraction in attractions)
            {
                attraction.Location = locationMap[attraction.LocationId];
                attraction.Category = categoryMap[attraction.CategoryId];
                attraction.Languages = attractionLangs
                    .Where(x => x.AttractionId == attraction.Id)
                    .Select(x => languageMap[x.LanguageId])
                    .ToList();
            }

            return (attractions, count);
        }
    }
}