namespace LocationService.UI.Controller
{
    using LocationService.Core.Domain.Dto.Request;
    using LocationService.Core.Domain.Dto.Response;
    using LocationService.Core.Domain.Entity;
    using LocationService.Core.Service;
    using Microsoft.AspNetCore.Mvc;

    [ApiController]
    [Route("location_service/api/public/v1/countries")]
    public class CountryController : ControllerBase
    {
        private readonly ICountryService countryService;

        public CountryController(ICountryService countryService)
        {
            this.countryService = countryService;
        }

        [HttpPost]
        public async Task<IActionResult> CreateCountry([FromBody] CreateCountryDto countryDto)
        {
            var createdCountry = await countryService.CreateCountryAsync(countryDto);
            return Ok(Resource<CountryEntity>.Success(createdCountry));
        }

    }
}