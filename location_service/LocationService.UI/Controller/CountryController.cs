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

        public static int DEFAULT_PAGE = 0;
        public static int DEFAULT_PAGE_SIZE = 10;

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

        [HttpGet]
        public async Task<IActionResult> GetCountries([FromQuery] CountryParams countryParams)
        {
            if (countryParams.Page <= 0)
            {
                countryParams.Page = DEFAULT_PAGE;
            }
            if (countryParams.PageSize <= 0)
            {
                countryParams.PageSize = DEFAULT_PAGE_SIZE;
            }

            var (countries, count) = await countryService.GetCountries(countryParams);
            var response = new PagedResponse<CountryEntity>
            {
                Data = countries,
                PageInfo = PageInfo.ToPageInfo(countryParams.Page, countryParams.PageSize, (int)count),
            };
            return Ok(Resource<object>.Success(response));
        }

    }
}