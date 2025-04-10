using LocationService.Core.Domain.Constant;
using LocationService.Core.Domain.Dto.Response;
using LocationService.Core.Domain.Entity;
using LocationService.Core.Exception;
using LocationService.Core.Service;
using Microsoft.AspNetCore.Mvc;

namespace LocationService.UI.Controller
{
    [ApiController]
    [Route("location_service/api/public/v1/trending")]
    public class TrendingPlaceController : ControllerBase
    {
        private readonly ITrendingPlaceService _trendingPlaceService;
        private readonly ILogger<TrendingPlaceController> _logger;

        public TrendingPlaceController(
            ITrendingPlaceService trendingPlaceService,
            ILogger<TrendingPlaceController> logger)
        {
            _trendingPlaceService = trendingPlaceService;
            _logger = logger;
        }

        [HttpGet]
        public async Task<IActionResult> GetTrendingPlaces([FromQuery] int page = 0, [FromQuery] int pageSize = 10)
        {
            // Apply pagination validations
            page = Math.Max(1, page);
            pageSize = Math.Clamp(pageSize, 1, 50);

            var response = await _trendingPlaceService.GetTrendingPlaces(page, pageSize);
            return Ok(Resource<object>.Success(response));
        }

        [HttpGet("type/{type}")]
        public async Task<IActionResult> GetTrendingPlacesByType(
            [FromRoute] string type,
            [FromQuery] int page = 1,
            [FromQuery] int pageSize = 10)
        {
            if (string.IsNullOrWhiteSpace(type))
            {
                _logger.LogError("Invalid type parameter provided");
                throw new AppException(ErrorCode.INVALID_PARAMETER);
            }
            page = Math.Max(0, page);
            pageSize = Math.Clamp(pageSize, 1, 50);

            var response = await _trendingPlaceService.GetTrendingPlacesByType(type, page, pageSize);

            return Ok(Resource<object>.Success(response));
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> GetTrendingPlaceById([FromRoute] long id)
        {
            var trendingPlace = await _trendingPlaceService.GetTrendingPlaceById(id);

            return Ok(Resource<TrendingPlaceEntity>.Success(trendingPlace));
        }
    }
}