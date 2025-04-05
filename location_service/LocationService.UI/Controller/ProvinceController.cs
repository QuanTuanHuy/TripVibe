namespace LocationService.UI.Controller
{
    using LocationService.Core.Domain.Dto.Request;
    using LocationService.Core.Domain.Dto.Response;
    using LocationService.Core.Domain.Entity;
    using LocationService.Core.Service;
    using Microsoft.AspNetCore.Mvc;

    [ApiController]
    [Route("location_service/api/public/v1/provinces")]
    public class ProvinceController : ControllerBase
    {
        private readonly IProvinceService _provinceService;

        public static int DEFAULT_PAGE = 0;
        public static int DEFAULT_PAGE_SIZE = 10;

        public ProvinceController(IProvinceService provinceService)
        {
            _provinceService = provinceService;
        }

        [HttpPost]
        public async Task<IActionResult> CreateProvince([FromBody] CreateProvinceDto req)
        {
            var createdProvince = await _provinceService.CreateProvinceAsync(req);
            return Ok(Resource<ProvinceEntity>.Success(createdProvince));
        }

        [HttpGet]
        public async Task<IActionResult> GetProvinces([FromQuery] ProvinceParams provinceParams)
        {
            if (provinceParams.Page <= 0)
            {
                provinceParams.Page = DEFAULT_PAGE;
            }
            if (provinceParams.PageSize <= 0)
            {
                provinceParams.PageSize = DEFAULT_PAGE_SIZE;
            }

            var (provinces, count) = await _provinceService.GetProvincesAsync(provinceParams);
            var response = new PagedResponse<ProvinceEntity>
            {
                Data = provinces,
                PageInfo = PageInfo.ToPageInfo(provinceParams.Page, provinceParams.PageSize, (int)count),
            };

            return Ok(Resource<object>.Success(response));
        }
    }
}