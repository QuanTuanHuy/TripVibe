using LocationService.Core.Domain.Entity;
using LocationService.Core.Port;

namespace LocationService.Core.UseCase
{
    public interface IGetLanguageUseCase
    {
        Task<List<LanguageEntity>> GetLanguagesByIdsAsync(List<long> ids);
    }

    public class GetLanguageUseCase : IGetLanguageUseCase
    {
        private readonly ILanguagePort _languagePort;

        public GetLanguageUseCase(ILanguagePort languagePort)
        {
            _languagePort = languagePort;
        }

        public async Task<List<LanguageEntity>> GetLanguagesByIdsAsync(List<long> ids)
        {
            return await _languagePort.GetLanguagesByIdsAsync(ids);
        }
    }
}