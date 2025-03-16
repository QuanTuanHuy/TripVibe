using Microsoft.EntityFrameworkCore;
using PromotionService.Core.Domain.Port;
using PromotionService.Core.Domain.Service;
using PromotionService.Core.Domain.Service.Impl;
using PromotionService.Core.Domain.UseCase;
using PromotionService.Core.Domain.UseCase.Impl;
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

// Register use cases
builder.Services.AddScoped<ICreatePromotionTypeUseCase, CreatePromotionTypeUseCase>();

// Register services
builder.Services.AddScoped<IPromotionTypeService, PromotionTypeService>();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseHttpsRedirection();
app.MapControllers();

app.Run();
