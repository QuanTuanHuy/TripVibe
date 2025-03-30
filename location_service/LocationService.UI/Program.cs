using LocationService.Core.Port;
using LocationService.Core.Service;
using LocationService.Core.UseCase;
using LocationService.Infrastructure.Repository;
using LocationService.Infrastructure.Repository.Adapter;
using LocationService.UI.Middleware;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();

// Redis configuration
// builder.Services.AddSingleton<JsonUtils>();
// builder.Services.AddSingleton<IConnectionMultiplexer>(sp => 
// {
//     string redisConnection = builder.Configuration.GetConnectionString("Redis") ?? "localhost:6379";
//     return ConnectionMultiplexer.Connect(redisConnection);
// });
// builder.Services.AddSingleton<ICachePort, RedisCacheAdapter>();

// Configure database
builder.Services.AddDbContext<LocationDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

// Register repositories
builder.Services.AddScoped<ICountryPort, CountryAdapter>();
builder.Services.AddScoped<IDbTransactionPort, DbTransactionAdapter>();


// Register use cases
builder.Services.AddScoped<ICreateCountryUseCase, CreateCountryUseCase>();


// Register services
builder.Services.AddScoped<ICountryService, CountryService>();
var app = builder.Build();

// middleware
app.UseMiddleware<ExceptionHandlingMiddleware>();


// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseHttpsRedirection();
app.MapControllers();

app.Run();