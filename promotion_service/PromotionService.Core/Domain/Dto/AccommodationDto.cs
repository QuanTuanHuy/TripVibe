namespace PromotionService.Core.Domain.Dto
{
    public class AccommodationDto
    {
        public long Id { get; set; }
        public string Name { get; set; }
        public long HostId { get; set; }
        public long TypeId { get; set; }
        public bool IsVerified { get; set; }
        public List<UnitDto> Units { get; set; }
    }

    public class UnitDto
    {
        public long Id { get; set; }
        public string Name { get; set; }
        public long AccommodationId { get; set; }
    }
}