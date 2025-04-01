namespace LocationService.Infrastructure.Repository
{
    using LocationService.Infrastructure.Repository.Model;
    using Microsoft.EntityFrameworkCore;

    public class LocationDbContext : DbContext
    {
        public LocationDbContext(DbContextOptions<LocationDbContext> options) : base(options)
        {
        }

        public DbSet<CountryModel> Countries { get; set; } = null!;
        public DbSet<ProvinceModel> Provinces { get; set; } = null!;
        public DbSet<LocationModel> Locations { get; set; } = null!;
        public DbSet<CategoryModel> Categories { get; set; } = null!;
        public DbSet<LanguageModel> Languages { get; set; } = null!;
    }
}