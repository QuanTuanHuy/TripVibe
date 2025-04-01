using LocationService.Core.Domain.Dto.Request;
using LocationService.Core.Domain.Entity;
using LocationService.Core.UseCase;

namespace LocationService.Core.Service
{
    public interface IAttractionService
    {
        Task<AttractionEntity> CreateAttractionAsync(CreateAttractionDto req);
    }

    public class AttractionService : IAttractionService
    {
        private readonly ICreateAttractionUseCase _createAttractionUseCase;

        public AttractionService(ICreateAttractionUseCase createAttractionUseCase)
        {
            _createAttractionUseCase = createAttractionUseCase;
        }

        public async Task<AttractionEntity> CreateAttractionAsync(CreateAttractionDto req)
        {
            return await _createAttractionUseCase.CreateAttractionAsync(req);
        }
    }
}