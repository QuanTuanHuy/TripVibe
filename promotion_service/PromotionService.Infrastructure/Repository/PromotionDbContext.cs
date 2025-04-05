using Microsoft.EntityFrameworkCore;
using PromotionService.Infrastructure.Repository.Model;

namespace PromotionService.Infrastructure.Repository;

public class PromotionDbContext : DbContext
{
    public PromotionDbContext(DbContextOptions<PromotionDbContext> options) : base(options)
    {
    }
    
    public DbSet<PromotionTypeModel> PromotionTypes { get; set; } = null!;
    public DbSet<ConditionModel> Conditions { get; set; } = null!;
    public DbSet<PromotionConditionModel> PromotionConditions { get; set; } = null!;
    public DbSet<PromotionModel> Promotions { get; set; } = null!;
    public DbSet<PromotionUnitModel> PromotionUnits { get; set; } = null!;
    public DbSet<PromotionTypeConditionModel> PromotionTypeConditions { get; set; } = null!;
    
}