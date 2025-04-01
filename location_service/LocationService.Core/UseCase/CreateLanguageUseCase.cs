namespace LocationService.Core.UseCase
{
    using System.Threading.Tasks;
    using LocationService.Core.Domain.Constant;
    using LocationService.Core.Domain.Dto.Request;
    using LocationService.Core.Domain.Entity;
    using LocationService.Core.Exception;
    using LocationService.Core.Port;

    public interface ICreateLanguageUseCase
    {
        Task<LanguageEntity> CreateLanguageAsync(CreateLanguageDto req);
    }

    public class CreateLanguageUseCase : ICreateLanguageUseCase
    {
        private readonly ILanguagePort _languagePort;
        private readonly IDbTransactionPort _dbTransactionPort;

        public CreateLanguageUseCase(ILanguagePort languagePort, IDbTransactionPort dbTransactionPort)
        {
            _dbTransactionPort = dbTransactionPort;
            _languagePort = languagePort;
        }

        public async Task<LanguageEntity> CreateLanguageAsync(CreateLanguageDto req)
        {
            var exisitedLanguage = await _languagePort.GetLanguageByCodeAsync(req.Code);
            if (exisitedLanguage != null)
            {
                throw new AppException(ErrorCode.LANGUAGE_CODE_ALREADY_EXISTS);
            }

            exisitedLanguage = await _languagePort.GetLanguageByNameAsync(req.Name);
            if (exisitedLanguage != null)
            {
                throw new AppException(ErrorCode.LANGUAGE_NAME_ALREADY_EXISTS);
            }

            var language = req.ToEntity();
            await _dbTransactionPort.ExecuteInTransactionAsync(async () =>
            {
                language = await _languagePort.CreateLanguageAsync(language);
            });
            return language;
        }
    }
}