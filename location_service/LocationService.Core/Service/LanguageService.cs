namespace LocationService.Core.Service
{
    using System.Threading.Tasks;
    using LocationService.Core.Domain.Dto.Request;
    using LocationService.Core.Domain.Entity;
    using LocationService.Core.UseCase;

    public interface ILanguageService
    {
        Task<LanguageEntity> CreateLanguageAsync(CreateLanguageDto languageDto);
    }

    public class LanguageService : ILanguageService
    {
        private readonly ICreateLanguageUseCase _createLanguageUseCase;

        public LanguageService(ICreateLanguageUseCase createLanguageUseCase)
        {
            _createLanguageUseCase = createLanguageUseCase;
        }

        public async Task<LanguageEntity> CreateLanguageAsync(CreateLanguageDto languageDto)
        {
            return await _createLanguageUseCase.CreateLanguageAsync(languageDto);
        }
    }
}