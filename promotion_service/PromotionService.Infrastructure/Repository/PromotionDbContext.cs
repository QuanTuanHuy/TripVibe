using Microsoft.EntityFrameworkCore;
using PromotionService.Infrastructure.Repository.Model;

namespace PromotionService.Infrastructure.Repository;

public class PromotionDbContext : DbContext
{
    public PromotionDbContext(DbContextOptions<PromotionDbContext> options) : base(options)
    {
    }
    
    public DbSet<PromotionTypeModel> PromotionTypes { get; set; } = null!;
    
    // protected override void OnModelCreating(ModelBuilder modelBuilder)
    // {
    //     modelBuilder.Entity<PromotionTypeModel>().ToTable("promotion_types");
    // }
}