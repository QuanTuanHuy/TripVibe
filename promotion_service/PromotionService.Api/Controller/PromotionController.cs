using Microsoft.AspNetCore.Mvc;
using PromotionService.Core.Domain.Dto.Request;
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
}