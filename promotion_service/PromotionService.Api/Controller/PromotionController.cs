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
        return Ok(Resource<PromotionEntity>.Success(createdPromotion));
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

        return Ok(Resource<PagedResponse<PromotionEntity>>.Success(response));
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetDetailPromotionAsync(long id)
    {
        var promotion = await _promotionService.GetDetailPromotionAsync(id);
        return Ok(Resource<PromotionEntity>.Success(promotion));
    }

    [HttpPut("{id}")]
    public async Task<IActionResult> UpdatePromotionAsync(long id, [FromBody] UpdatePromotionDto req)
    {
        var updatedPromotion = await _promotionService.UpdatePromotionAsync(1, id, req);
        return Ok(Resource<PromotionEntity>.Success(updatedPromotion));
    }

    [HttpPut("{id}/stop")]
    public async Task<IActionResult> StopPromotionAsync(long id)
    {
        // now hardcoding the userId to 1
        await _promotionService.StopPromotionAsync(1, id);
        return Ok(Resource<bool>.Success(true));
    }

    [HttpPost("verify")]
    public async Task<IActionResult> VerifyPromotion([FromBody] VerifyPromotionRequest request)
    {
        var response = await _promotionService.VerifyPromotion(request);
        return Ok(Resource<VerifyPromotionResponse>.Success(response));
    }

    [HttpPut("update_usage")]
    public async Task<IActionResult> UpdatePromotionUsage([FromBody] UpdatePromotionUsageRequest request)
    {
        var result = await _promotionService.UpdatePromotionUsage(request.PromotionIds);
        return Ok(Resource<bool>.Success(result));
    }
}