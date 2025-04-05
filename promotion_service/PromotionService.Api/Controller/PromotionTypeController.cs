using Microsoft.AspNetCore.Mvc;
using Microsoft.OpenApi.Any;
using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Dto.Response;
using PromotionService.Core.Domain.Entity;
using PromotionService.Core.Service;

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

    [HttpGet]
    public async Task<IActionResult> GetPromotionTypesAsync([FromQuery] PromotionTypeParams queryParams)
    {
        var (items, totalCount) = await _promotionTypeService.GetPromotionTypesAsync(queryParams);

        var response = new PagedResponse<PromotionTypeEntity>
        {
            Data = items,
            PageInfo = PageInfo.toPageInfo(queryParams.Page, queryParams.PageSize, totalCount)
        };

        return Ok(response);

    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetPromotionTypeByIdAsync(long id)
    {
        var promotionType = await _promotionTypeService.GetPromotionTypeByIdAsync(id);
        return Ok(Resource<PromotionTypeEntity>.Success(promotionType));
    }
    
    [HttpPut("{id}")]
    public async Task<IActionResult> UpdatePromotionTypeAsync(long id, [FromBody] UpdatePromotionTypeDto req)
    {
        var updatedPromotionType = await _promotionTypeService.UpdatePromotionTypeAsync(id, req);
        return Ok(updatedPromotionType);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeletePromotionTypeAsync(long id)
    {
        await _promotionTypeService.DeletePromotionTypeAsync(id);
        return Ok(Resource<object>.Success(null));
    }

    [HttpPost("{id}/conditions")]
    public async Task<IActionResult> AddConditionsToPromotionTypeAsync(long id, [FromBody] AddConditionToPromotionTypeDto req)
    {
        var conditions = await _promotionTypeService.AddConditionsToPromotionTypeAsync(id, req);
        return Ok(Resource<List<PromotionTypeConditionEntity>>.Success(conditions));
    }
}