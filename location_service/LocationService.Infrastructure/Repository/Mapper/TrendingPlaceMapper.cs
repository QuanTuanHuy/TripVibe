using LocationService.Core.Domain.Entity;
using LocationService.Infrastructure.Repository.Model;
using System.Text.Json;

namespace LocationService.Infrastructure.Repository.Mapper
{
    public class TrendingPlaceMapper
    {
        public static TrendingPlaceEntity ToEntity(TrendingPlaceModel trendingPlace)
        {
            if (trendingPlace == null) return null;

            return new TrendingPlaceEntity
            {
                Id = trendingPlace.Id,
                Name = trendingPlace.Name,
                Description = trendingPlace.Description,
                Type = trendingPlace.Type,
                ReferenceId = trendingPlace.ReferenceId,
                Rank = trendingPlace.Rank,
                StartDate = trendingPlace.StartDate,
                EndDate = trendingPlace.EndDate,
                IsActive = trendingPlace.IsActive,
                Tags = trendingPlace.Tags,
                ImageUrl = trendingPlace.ImageUrl,
                Score = trendingPlace.Score,
                Metadata = string.IsNullOrEmpty(trendingPlace.Metadata)
                    ? new Dictionary<string, object>()
                    : JsonSerializer.Deserialize<Dictionary<string, object>>(trendingPlace.Metadata) ?? new Dictionary<string, object>(),
                CreatedAt = trendingPlace.CreatedAt,
                UpdatedAt = trendingPlace.UpdatedAt
            };
        }

        public static TrendingPlaceModel ToModel(TrendingPlaceEntity trendingPlace)
        {
            if (trendingPlace == null) return null;

            return new TrendingPlaceModel
            {
                Id = trendingPlace.Id,
                Name = trendingPlace.Name,
                Description = trendingPlace.Description,
                Type = trendingPlace.Type,
                ReferenceId = trendingPlace.ReferenceId,
                Rank = trendingPlace.Rank,
                StartDate = trendingPlace.StartDate,
                EndDate = trendingPlace.EndDate,
                IsActive = trendingPlace.IsActive,
                Tags = trendingPlace.Tags,
                ImageUrl = trendingPlace.ImageUrl,
                Score = trendingPlace.Score,
                Metadata = trendingPlace.Metadata != null
                    ? JsonSerializer.Serialize(trendingPlace.Metadata)
                    : string.Empty,
                CreatedAt = trendingPlace.CreatedAt,
                UpdatedAt = trendingPlace.UpdatedAt
            };
        }

        public static List<TrendingPlaceEntity> ToEntityList(List<TrendingPlaceModel> trendingPlaces)
        {
            if (trendingPlaces == null) return null;

            var entities = new List<TrendingPlaceEntity>();
            foreach (var trendingPlace in trendingPlaces)
            {
                entities.Add(ToEntity(trendingPlace));
            }
            return entities;
        }

        public static List<TrendingPlaceModel> ToModelList(List<TrendingPlaceEntity> trendingPlaces)
        {
            if (trendingPlaces == null) return null;

            var models = new List<TrendingPlaceModel>();
            foreach (var trendingPlace in trendingPlaces)
            {
                models.Add(ToModel(trendingPlace));
            }
            return models;
        }
    }
}
