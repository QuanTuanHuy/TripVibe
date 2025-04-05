namespace LocationService.Core.Domain.Dto.Request
{
    public class ProvinceParams : BaseParams
    {
        public string? Name { get; set; }
        public string? Code { get; set; }
        public int? CountryId { get; set; }
    }
}