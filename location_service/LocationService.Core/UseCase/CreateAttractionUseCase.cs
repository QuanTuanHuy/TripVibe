namespace LocationService.Core.UseCase
{
    using System.Threading.Tasks;
    using LocationService.Core.Domain.Dto.Request;
    using LocationService.Core.Domain.Entity;
    using LocationService.Core.Domain.Constant;
    using LocationService.Core.Port;
    using Microsoft.Extensions.Logging;
    using LocationService.Core.Exception;

    public interface ICreateAttractionUseCase
    {
        Task<AttractionEntity> CreateAttractionAsync(CreateAttractionDto request);
    }

    public class CreateAttractionUseCase : ICreateAttractionUseCase
    {
        private readonly IAttractionPort _attractionPort;
        private readonly IImagePort _imagePort;
        private readonly IAttractionSchedulePort _schedulePort;
        private readonly IAttractionLanguagePort _attractionLanguagePort;
        private readonly IDbTransactionPort _dbTransactionPort;
        private readonly ICreateLocationUseCase _createLocationUseCase;
        private readonly IGetLanguageUseCase _getLanguageUseCase;
        private readonly ILogger<CreateAttractionUseCase> _logger;

        public CreateAttractionUseCase(
            IAttractionPort attractionPort,
            IImagePort imagePort,
            IAttractionSchedulePort schedulePort,
            IDbTransactionPort dbTransactionPort,
            IAttractionLanguagePort attractionLanguagePort,
            ICreateLocationUseCase createLocationUseCase,
            IGetLanguageUseCase getLanguageUseCase,
            ILogger<CreateAttractionUseCase> logger)
        {
            _attractionPort = attractionPort;
            _imagePort = imagePort;
            _schedulePort = schedulePort;
            _attractionLanguagePort = attractionLanguagePort;
            _dbTransactionPort = dbTransactionPort;
            _createLocationUseCase = createLocationUseCase;
            _getLanguageUseCase = getLanguageUseCase;
            _logger = logger;
        }

        public async Task<AttractionEntity> CreateAttractionAsync(CreateAttractionDto request)
        {
            var existingAttraction = await _attractionPort.GetAttractionByNameAsync(request.Name);
            if (existingAttraction != null)
            {
                _logger.LogWarning("Attraction with name {Name} already exists", request.Name);
                throw new AppException(ErrorCode.ATTRACTION_NAME_ALREADY_EXISTS);
            }

            var attraction = request.ToEntity();

            await _dbTransactionPort.ExecuteInTransactionAsync(async () =>
            {
                var location = await _createLocationUseCase.CreateLocationAsync(request.Location);
                attraction.LocationId = location.Id;
                attraction = await _attractionPort.CreateAttractionAsync(attraction);

                // Create images
                if (request.Images != null && request.Images.Any())
                {
                    // Validate primary image
                    var primaryImageCount = request.Images.Count(i => i.IsPrimary);
                    if (primaryImageCount == 0 && request.Images.Any())
                    {
                        request.Images.First().IsPrimary = true;
                        _logger.LogInformation("No primary image specified, setting first image as primary");
                    }
                    else if (primaryImageCount > 1)
                    {
                        _logger.LogWarning("Multiple primary images specified, using only the first one");
                        var firstPrimary = true;
                        foreach (var img in request.Images)
                        {
                            if (img.IsPrimary)
                            {
                                if (!firstPrimary)
                                {
                                    img.IsPrimary = false;
                                }
                                firstPrimary = false;
                            }
                        }
                    }

                    var images = request.Images.Select(i => new ImageEntity
                    {
                        entityId = attraction.Id,
                        EntityType = ImageEntityType.Attraction,
                        Url = i.Url,
                        IsPrimary = i.IsPrimary
                    }).ToList();

                    images = await _imagePort.CreateImagesAsync(images);
                    attraction.Images = images;
                }

                // Create languages
                if (request.languageIds != null && request.languageIds.Any())
                {
                    var existingLanguages = await _getLanguageUseCase.GetLanguagesByIdsAsync(request.languageIds);
                    if (existingLanguages.Count != request.languageIds.Count)
                    {
                        _logger.LogWarning("Some languages do not exist, please check the language IDs");
                        throw new AppException(ErrorCode.LANGUAGE_NOT_FOUND);
                    }
                    var languages = request.languageIds.Select(l => new AttractionLanguageEntity
                    {
                        AttractionId = attraction.Id,
                        LanguageId = l
                    }).ToList();

                    languages = await _attractionLanguagePort.CreateAttractionLanguagesAsync(languages);
                }

                // Create schedules
                var schedules = new List<AttractionScheduleEntity>();
                if (request.Schedules != null && request.Schedules.Any())
                {
                    foreach (var scheduleDto in request.Schedules)
                    {
                        var schedule = scheduleDto.ToEntity();
                        schedule.AttractionId = attraction.Id;

                        schedules.Add(schedule);
                    }
                    schedules = await _schedulePort.CreateSchedulesAsync(schedules);
                    attraction.Schedules = schedules;
                }
            });
            return attraction;
        }
    }
}
