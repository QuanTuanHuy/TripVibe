using Microsoft.AspNetCore.Mvc;
using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Dto.Response;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Service;

namespace PromotionService.Api.Controller;

[ApiController]
[Route("promotion_service/api/public/v1/promotions")]
public class PromotionController : ControllerBase
{
    private readonly IPromotionService _promotionService;

    public PromotionController(IPromotionService promotionService)
    {
        _promotionService = promotionService;
    }

    [HttpPost]
    public async Task<IActionResult> CreatePromotionAsync([FromBody] CreatePromotionDto req)
    {
        var createdPromotion = await _promotionService.CreatePromotionAsync(req);
        return Ok(createdPromotion);
    }

    [HttpGet]
    public async Task<IActionResult> GetPromotionsAsync([FromQuery] PromotionParams queryParams)
    {
        var (items, totalCount) = await _promotionService.GetPromotionsAsync(queryParams);

        var response = new PagedResponse<PromotionEntity>
        {
            Data = items,
            PageInfo = PageInfo.toPageInfo(queryParams.Page, queryParams.PageSize, totalCount)
        };

        return Ok(response);
    }
}