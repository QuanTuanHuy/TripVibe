namespace LocationService.UI.Controller
{
    using System.Threading.Tasks;
    using LocationService.Core.Domain.Dto.Request;
    using LocationService.Core.Domain.Dto.Response;
    using LocationService.Core.Domain.Entity;
    using LocationService.Core.Service;
    using Microsoft.AspNetCore.Mvc;

    [ApiController]
    [Route("location_service/api/public/v1/attractions")]
    public class AttractionController : ControllerBase
    {
        public static int DEFAULT_PAGE = 0;
        public static int DEFAULT_PAGE_SIZE = 10;

        private readonly IAttractionService _attractionService;

        public AttractionController(IAttractionService attractionService)
        {
            _attractionService = attractionService;
        }

        [HttpPost]
        public async Task<IActionResult> CreateAttraction([FromBody] CreateAttractionDto request)
        {
            var attraction = await _attractionService.CreateAttractionAsync(request);
            return Ok(Resource<AttractionEntity>.Success(attraction));
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> GetAttractionDetail(long id)
        {
            var attraction = await _attractionService.GetDetailAttractionAsync(id);
            return Ok(Resource<AttractionEntity>.Success(attraction));
        }

        [HttpGet]
        public async Task<IActionResult> GetAttractions([FromQuery] GetAttractionParams parameters)
        {
            if (parameters.Page <= 0)
            {
                parameters.Page = DEFAULT_PAGE;
            }
            if (parameters.PageSize <= 0)
            {
                parameters.PageSize = DEFAULT_PAGE_SIZE;
            }
            var (attractions, count) = await _attractionService.GetAttractionsAsync(parameters);
            var response = new PagedResponse<AttractionEntity>
            {
                Data = attractions,
                PageInfo = PageInfo.ToPageInfo(parameters.Page, parameters.PageSize, (int)count),
            };
            return Ok(Resource<object>.Success(response));
        }

    }
}