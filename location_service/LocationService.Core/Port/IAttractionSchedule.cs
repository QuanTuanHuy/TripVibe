namespace LocationService.Core.Port
{
    using System.Threading.Tasks;
    using System.Collections.Generic;
    using LocationService.Core.Domain.Entity;

    public interface IAttractionSchedulePort
    {
        Task<List<AttractionScheduleEntity>> CreateSchedulesAsync(List<AttractionScheduleEntity> schedules);
        Task<List<AttractionScheduleEntity>> GetSchedulesByAttractionIdAsync(long attractionId);
    }
}