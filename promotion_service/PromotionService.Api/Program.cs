using Microsoft.EntityFrameworkCore;
using PromotionService.Api.Middleware;
using PromotionService.Core.Domain.Port;
using PromotionService.Core.Port;
using PromotionService.Core.Service;
using PromotionService.Core.Service.Impl;
using PromotionService.Core.UseCase;
using PromotionService.Core.UseCase.Impl;
using PromotionService.Infrastructure.Redis;
using PromotionService.Infrastructure.Repository;
using PromotionService.Infrastructure.Repository.Adapter;
using PromotionService.Kernel.Utils;
using StackExchange.Redis;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();

// Redis configuration
builder.Services.AddSingleton<JsonUtils>();
builder.Services.AddSingleton<IConnectionMultiplexer>(sp => 
{
    string redisConnection = builder.Configuration.GetConnectionString("Redis") ?? "localhost:6379";
    return ConnectionMultiplexer.Connect(redisConnection);
});
builder.Services.AddSingleton<ICachePort, RedisCacheAdapter>();

// Configure database
builder.Services.AddDbContext<PromotionDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

// Register repositories
builder.Services.AddScoped<IPromotionTypePort, PromotionTypeAdapter>();
builder.Services.AddScoped<IDbTransactionPort, DbTransactionAdapter>();
builder.Services.AddScoped<IConditionPort, ConditionAdapter>();
builder.Services.AddScoped<IPromotionConditionPort, PromotionConditionAdapter>();
builder.Services.AddScoped<IPromotionPort, PromotionAdapter>();
builder.Services.AddScoped<IPromotionUnitPort, PromotionUnitAdapter>();
builder.Services.AddScoped<IPromotionTypeConditionPort, PromotionTypeConditionAdapter>();


// Register use cases
builder.Services.AddScoped<ICreatePromotionTypeUseCase, CreatePromotionTypeUseCase>();
builder.Services.AddScoped<IGetPromotionTypeUseCase, GetPromotionTypeUseCase>();
builder.Services.AddScoped<IUpdatePromotionTypeUseCase, UpdatePromotionTypeUseCase>();
builder.Services.AddScoped<ICreateConditionUseCase, CreateConditionUseCase>();
builder.Services.AddScoped<ICreatePromotionUseCase, CreatePromotionUseCase>();
builder.Services.AddScoped<IGetConditionUseCase, GetConditionUseCase>();
builder.Services.AddScoped<IGetPromotionConditionUseCase, GetPromotionConditionUseCase>();
builder.Services.AddScoped<IGetPromotionUseCase, GetPromotionUseCase>();
builder.Services.AddScoped<IGetPromotionUnitUseCase, GetPromotionUnitUseCase>();
builder.Services.AddScoped<IUpdatePromotionUseCase, UpdatePromotionUseCase>();
builder.Services.AddScoped<IDeletePromotionTypeUseCase, DeletePromotionTypeUseCase>();
builder.Services.AddScoped<IUpdateConditionUseCase, UpdateConditionUseCase>();
builder.Services.AddScoped<IAddConditionToPromotionTypeUseCase, AddConditionToPromotionTypeUseCase>();

// Register services
builder.Services.AddScoped<IPromotionTypeService, PromotionTypeService>();
builder.Services.AddScoped<IConditionService, ConditionService>();
builder.Services.AddScoped<IPromotionService, PromotionService.Core.Service.Impl.PromotionService>();
var app = builder.Build();

app.UseMiddleware<ExceptionHandlingMiddleware>();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseHttpsRedirection();
app.MapControllers();

app.Run();
