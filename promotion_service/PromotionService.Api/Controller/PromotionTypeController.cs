using Microsoft.AspNetCore.Mvc;
using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Domain.Service;

namespace PromotionService.Api.Controller;

[ApiController]
[Route("promotion_service/api/public/v1/promotion_types")]
public class PromotionTypeController : ControllerBase
{
    private readonly IPromotionTypeService _promotionTypeService;
    
    public PromotionTypeController(IPromotionTypeService promotionTypeService)
    {
        _promotionTypeService = promotionTypeService;
    }
    
    [HttpPost]
    public async Task<IActionResult> CreatePromotionTypeAsync([FromBody] CreatePromotionTypeDto req)
    {
        var createdPromotionType = await _promotionTypeService.CreatePromotionTypeAsync(req.toEntity());
        return Ok(createdPromotionType);
    }
}