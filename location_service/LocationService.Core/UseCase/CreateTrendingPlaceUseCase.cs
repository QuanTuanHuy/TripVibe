using LocationService.Core.Domain.Constant;
using LocationService.Core.Domain.Entity;
using LocationService.Core.Exception;
using LocationService.Core.Port;
using Microsoft.Extensions.Logging;

namespace LocationService.Core.UseCase
{
    public interface ICreateTrendingPlaceUseCase
    {
        Task<TrendingPlaceEntity> CreateTrendingPlace(TrendingPlaceEntity trendingPlace);
    }

    public class CreateTrendingPlaceUseCase : ICreateTrendingPlaceUseCase
    {
        private readonly ITrendingPlacePort _trendingPlacePort;
        private readonly IDbTransactionPort _dbTransactionPort;
        private readonly ILogger<CreateTrendingPlaceUseCase> _logger;

        public CreateTrendingPlaceUseCase(ITrendingPlacePort trendingPlacePort,
            IDbTransactionPort dbTransactionPort,
            ILogger<CreateTrendingPlaceUseCase> logger)
        {
            _trendingPlacePort = trendingPlacePort;
            _dbTransactionPort = dbTransactionPort;
            _logger = logger;
        }

        public async Task<TrendingPlaceEntity> CreateTrendingPlace(TrendingPlaceEntity trendingPlace) {
            var existedTrendingPlace = await _trendingPlacePort.GetByReferenceIdAndType(trendingPlace.ReferenceId, trendingPlace.Type);
            if (existedTrendingPlace != null) {
                _logger.LogError($"Trending place with reference ID {trendingPlace.ReferenceId} and type {trendingPlace.Type} already exists.");
                throw new AppException(ErrorCode.TRENDING_PLACE_ALREADY_EXISTS);
            }

            await _dbTransactionPort.ExecuteInTransactionAsync(async () =>
            {
                trendingPlace = await _trendingPlacePort.CreateTrendingPlace(trendingPlace);
            });

            return trendingPlace;
        }
    }
}