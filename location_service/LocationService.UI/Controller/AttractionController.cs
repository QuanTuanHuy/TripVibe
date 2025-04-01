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

    }
}