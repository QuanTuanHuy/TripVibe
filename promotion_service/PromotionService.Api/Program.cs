using Microsoft.EntityFrameworkCore;
using PromotionService.Core.Domain.Port;
using PromotionService.Core.Port;
using PromotionService.Core.Service;
using PromotionService.Core.Service.Impl;
using PromotionService.Core.UseCase;
using PromotionService.Core.UseCase.Impl;
using PromotionService.Infrastructure.Repository;
using PromotionService.Infrastructure.Repository.Adapter;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();

// Configure database
builder.Services.AddDbContext<PromotionDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

// Register repositories
builder.Services.AddScoped<IPromotionTypePort, PromotionTypeAdapter>();
builder.Services.AddScoped<IDbTransactionPort, DbTransactionAdapter>();
builder.Services.AddScoped<IConditionPort, ConditionAdapter>();

// Register use cases
builder.Services.AddScoped<ICreatePromotionTypeUseCase, CreatePromotionTypeUseCase>();
builder.Services.AddScoped<IGetPromotionTypeUseCase, GetPromotionTypeUseCase>();
builder.Services.AddScoped<IUpdatePromotionTypeUseCase, UpdatePromotionTypeUseCase>();
builder.Services.AddScoped<ICreateConditionUseCase, CreateConditionUseCase>();

// Register services
builder.Services.AddScoped<IPromotionTypeService, PromotionTypeService>();
builder.Services.AddScoped<IConditionService, ConditionService>();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseHttpsRedirection();
app.MapControllers();

app.Run();
