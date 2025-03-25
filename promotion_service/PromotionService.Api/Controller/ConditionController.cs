using Microsoft.AspNetCore.Mvc;
using PromotionService.Core.Domain.Dto.Request;
using PromotionService.Core.Domain.Dto.Response;
using PromotionService.Core.Domain.Entity;
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
        return Ok(Resource<ConditionEntity>.Success(createdCondition));
    }
    
}