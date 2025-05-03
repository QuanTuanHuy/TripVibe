using LocationService.Core.Domain.Port;
using LocationService.Core.Port;
using LocationService.Core.Service;
using LocationService.Core.UseCase;
using LocationService.Infrastructure.Redis;
using LocationService.Infrastructure.Repository;
using LocationService.Infrastructure.Repository.Adapter;
using LocationService.Kernel.Utils;
using LocationService.UI.Middleware;
using Microsoft.EntityFrameworkCore;
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
builder.Services.AddDbContext<LocationDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

// Register repositories
builder.Services.AddScoped<ICountryPort, CountryAdapter>();
builder.Services.AddScoped<IDbTransactionPort, DbTransactionAdapter>();
builder.Services.AddScoped<IProvincePort, ProvinceAdapter>();
builder.Services.AddScoped<ILocationPort, LocationAdapter>();
builder.Services.AddScoped<ICategoryPort, CategoryAdapter>();
builder.Services.AddScoped<ILanguagePort, LanguageAdapter>();
builder.Services.AddScoped<IAttractionPort, AttractionAdapter>();
builder.Services.AddScoped<IAttractionSchedulePort, AttractionScheduleAdapter>();
builder.Services.AddScoped<IImagePort, ImageAdapter>();
builder.Services.AddScoped<IAttractionLanguagePort, AttractionLanguageAdapter>();
builder.Services.AddScoped<ITrendingPlacePort, TrendingPlaceAdapter>();


// Register use cases
builder.Services.AddScoped<ICreateCountryUseCase, CreateCountryUseCase>();
builder.Services.AddScoped<IGetCountryUseCase, GetCountryUseCase>();
builder.Services.AddScoped<ICreateProvinceUseCase, CreateProvinceUseCase>();
builder.Services.AddScoped<IGetProvinceUseCase, GetProvinceUseCase>();
builder.Services.AddScoped<ICreateLocationUseCase, CreateLocationUseCase>();
builder.Services.AddScoped<IGetLocationUseCase, GetLocationUseCase>();
builder.Services.AddScoped<ICreateCategoryUseCase, CreateCategoryUseCase>();
builder.Services.AddScoped<IGetCategoryUseCase, GetCategoryUseCase>();
builder.Services.AddScoped<ICreateLanguageUseCase, CreateLanguageUseCase>();
builder.Services.AddScoped<ICreateAttractionUseCase, CreateAttractionUseCase>();
builder.Services.AddScoped<IGetLanguageUseCase, GetLanguageUseCase>();
builder.Services.AddScoped<IGetAttractionUseCase, GetAttractionUseCase>();
builder.Services.AddScoped<IGetImageUseCase, GetImageUseCase>();
builder.Services.AddScoped<ICreateTrendingPlaceUseCase, CreateTrendingPlaceUseCase>();
builder.Services.AddScoped<IGetTrendingPlaceUseCase, GetTrendingPlaceUseCase>();

// Register services
builder.Services.AddScoped<ICountryService, CountryService>();
builder.Services.AddScoped<IProvinceService, ProvinceService>();
builder.Services.AddScoped<ILocationService, LocationAppService>();
builder.Services.AddScoped<ICategoryService, CategoryService>();
builder.Services.AddScoped<ILanguageService, LanguageService>();
builder.Services.AddScoped<IAttractionService, AttractionService>();
builder.Services.AddScoped<ITrendingPlaceService, TrendingPlaceService>();

var app = builder.Build();

// middleware
app.UseMiddleware<ExceptionHandlingMiddleware>();


// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

// Remove the HTTPS redirection middleware
// app.UseHttpsRedirection();

app.MapControllers();

app.Run();