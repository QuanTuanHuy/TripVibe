using Microsoft.AspNetCore.Mvc;
using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Service;
namespace PromotionService.Api.Controller;

[ApiController]
[Route("promotion_service/api/public/v1/conditions")]
public class ConditionController : ControllerBase
{
    private readonly IConditionService _conditionService;

    public ConditionController(IConditionService conditionService)
    {
        _conditionService = conditionService;
    }

    [HttpPost]
    public async Task<IActionResult> CreateConditionAsync([FromBody] CreateConditionDto req)
    {
        var createdCondition = await _conditionService.CreateConditionAsync(req);
        return Ok(createdCondition);
    }

    // [HttpGet]
    // public async Task<IActionResult> GetConditionsAsync([FromQuery] ConditionParams queryParams)
    // {
    //     var (items, totalCount) = await _conditionService.GetConditionsAsync(queryParams);
    //
    //     var response = new PagedResponse<ConditionEntity>
    //     {
    //         Data = items,
    //         PageInfo = PageInfo.toPageInfo(queryParams.Page, queryParams.PageSize, totalCount)
    //     };
    //
    //     return Ok(response);
    //
    // }
    //
    // [HttpPut("{id}")]
    // public async Task<IActionResult> UpdateConditionAsync(long id, [FromBody] UpdateConditionDto req)
    // {
    //     var updatedCondition = await _conditionService.UpdateConditionAsync(id, req);
    //     return Ok(updatedCondition);
    // }
}