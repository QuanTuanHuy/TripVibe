namespace LocationService.UI.Controller
{
    using System.Threading.Tasks;
    using LocationService.Core.Domain.Dto.Request;
    using LocationService.Core.Domain.Dto.Response;
    using LocationService.Core.Domain.Entity;
    using LocationService.Core.Service;
    using Microsoft.AspNetCore.Mvc;

    [ApiController]
    [Route("location_service/api/public/v1/languages")]
    public class LanguageController : ControllerBase
    {
        private readonly ILanguageService _languageService;

        public LanguageController(ILanguageService languageService)
        {
            _languageService = languageService;
        }

        [HttpPost]
        public async Task<IActionResult> CreateLanguage([FromBody] CreateLanguageDto req)
        {
            var createdLanguage = await _languageService.CreateLanguageAsync(req);
            return Ok(Resource<LanguageEntity>.Success(createdLanguage));
        }
    }
}