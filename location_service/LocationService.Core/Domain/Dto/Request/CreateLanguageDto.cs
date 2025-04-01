using LocationService.Core.Domain.Entity;

namespace LocationService.Core.Domain.Dto.Request
{
    public class CreateLanguageDto
    {
        public string Name { get; set; } = string.Empty;
        public string Code { get; set; } = string.Empty;

        public LanguageEntity ToEntity()
        {
            return new LanguageEntity
            {
                Name = Name,
                Code = Code
            };
        }
    }
}