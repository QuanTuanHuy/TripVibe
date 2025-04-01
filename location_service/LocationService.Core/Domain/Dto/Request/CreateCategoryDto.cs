using LocationService.Core.Domain.Entity;

namespace LocationService.Core.Domain.Dto.Request
{
    public class CreateCategoryDto
    {
        public string Name { get; set; }
        public string Description { get; set; }
        public string IconUrl { get; set; }

        public CategoryEntity ToEntity()
        {
            return new CategoryEntity
            {
                Name = Name,
                Description = Description,
                IconUrl = IconUrl,
            };
        }
    }
}