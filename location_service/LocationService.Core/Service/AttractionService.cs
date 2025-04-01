using LocationService.Core.Domain.Dto.Request;
using LocationService.Core.Domain.Entity;
using LocationService.Core.UseCase;

namespace LocationService.Core.Service
{
    public interface IAttractionService
    {
        Task<AttractionEntity> CreateAttractionAsync(CreateAttractionDto req);
        Task<AttractionEntity> GetDetailAttractionAsync(long id);
        Task<(List<AttractionEntity> attractions, long count)> GetAttractionsAsync(GetAttractionParams parameters);
    }

    public class AttractionService : IAttractionService
    {
        private readonly ICreateAttractionUseCase _createAttractionUseCase;
        private readonly IGetAttractionUseCase _getAttractionUseCase;

        public AttractionService(
            ICreateAttractionUseCase createAttractionUseCase,
            IGetAttractionUseCase getAttractionUseCase)
        {
            _createAttractionUseCase = createAttractionUseCase;
            _getAttractionUseCase = getAttractionUseCase;
        }

        public async Task<AttractionEntity> CreateAttractionAsync(CreateAttractionDto req)
        {
            return await _createAttractionUseCase.CreateAttractionAsync(req);
        }

        public async Task<AttractionEntity> GetDetailAttractionAsync(long id)
        {
            return await _getAttractionUseCase.GetDetailAttractionAsync(id);
        }

        public async Task<(List<AttractionEntity> attractions, long count)> GetAttractionsAsync(GetAttractionParams parameters)
        {
            return await _getAttractionUseCase.GetAttractionsAsync(parameters);
        }
    }
}