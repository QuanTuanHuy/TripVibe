using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using LocationService.Core.Domain.Dto.Request;
using Npgsql;

namespace LocationService.Infrastructure.Repository.Specification
{
    public class AttractionSpecification
    {
        public static (string, List<NpgsqlParameter>) ToGetAttractionsSpecification(GetAttractionParams parameters)
        {
            var sql = new StringBuilder(@"
                SELECT a.* FROM attractions a ");

            var conditions = new List<string>();
            var parameters_list = new List<NpgsqlParameter>();

            // Apply joins
            if (parameters.CountryId != null || parameters.ProvinceId != null)
            {
                sql.Append(@"
                    INNER JOIN locations l ON a.location_id = l.id ");
            }

            if (parameters.LanguageIds != null && parameters.LanguageIds.Any())
            {
                sql.Append(@"
                    INNER JOIN attraction_languages al ON a.id = al.attraction_id ");
            }

            // Add joins for schedule filtering if needed
            if (parameters.FromDate != null || parameters.ToDate != null)
            {
                sql.Append(@"
                    LEFT JOIN attraction_schedules sch ON a.id = sch.attraction_id ");
            }

            // Apply where conditions
            ApplyWhereConditions(parameters, conditions, parameters_list);

            if (conditions.Any())
            {
                sql.Append(" WHERE " + string.Join(" AND ", conditions));
            }

            // Apply group by if using language filter
            if (parameters.LanguageIds != null && parameters.LanguageIds.Any())
            {
                sql.Append(" GROUP BY a.id ");

                // Having clause for language count match
                if (parameters.LanguageIds.Count > 1 && parameters.MatchAllLanguages)
                {
                    sql.Append(" HAVING COUNT(DISTINCT al.language_id) = @requiredLanguageCount");
                    parameters_list.Add(new NpgsqlParameter("@requiredLanguageCount", parameters.LanguageIds.Count));
                }
            }

            // Apply sorting
            sql.Append(" ORDER BY ");
            if (!string.IsNullOrEmpty(parameters.SortBy))
            {
                string sortField = GetSortField(parameters.SortBy);
                sql.Append($"a.{sortField} {(parameters.SortOrder == "ASC" ? "ASC" : "DESC")}");
            }
            else
            {
                sql.Append("a.created_at DESC");
            }

            // Add secondary sort for consistent pagination
            if (parameters.SortBy?.ToLower() != "id")
            {
                sql.Append(", a.id ASC");
            }

            // Apply pagination
            sql.Append(" LIMIT @PageSize OFFSET @Offset");
            parameters_list.Add(new NpgsqlParameter("@PageSize", parameters.PageSize));
            parameters_list.Add(new NpgsqlParameter("@Offset", parameters.Page * parameters.PageSize));

            return (sql.ToString(), parameters_list);
        }

        public static (string, List<NpgsqlParameter>) ToCountAttractionsSpecification(GetAttractionParams parameters)
        {
            var sql = new StringBuilder(@"
                SELECT COUNT(DISTINCT a.id) FROM attractions a ");

            var conditions = new List<string>();
            var parameters_list = new List<NpgsqlParameter>();

            // Apply joins if needed
            if (parameters.CountryId != null || parameters.ProvinceId != null)
            {
                sql.Append(@"
                    INNER JOIN locations l ON a.location_id = l.id ");
            }

            if (parameters.LanguageIds != null && parameters.LanguageIds.Any())
            {
                sql.Append(@"
                    INNER JOIN attraction_languages al ON a.id = al.attraction_id ");
            }

            // Add joins for schedule filtering if needed
            if (parameters.FromDate != null || parameters.ToDate != null)
            {
                sql.Append(@"
                    LEFT JOIN attraction_schedules sch ON a.id = sch.attraction_id ");
            }

            // Apply where conditions
            ApplyWhereConditions(parameters, conditions, parameters_list);

            if (conditions.Any())
            {
                sql.Append(" WHERE " + string.Join(" AND ", conditions));
            }

            // Apply having clause for language count match
            if (parameters.LanguageIds != null && parameters.LanguageIds.Count > 1 && parameters.MatchAllLanguages)
            {
                sql.Append(@" GROUP BY a.id HAVING COUNT(DISTINCT al.language_id) = @requiredLanguageCount");
                parameters_list.Add(new NpgsqlParameter("@requiredLanguageCount", parameters.LanguageIds.Count));
            }

            return (sql.ToString(), parameters_list);
        }

        private static void ApplyWhereConditions(GetAttractionParams parameters, List<string> conditions, List<NpgsqlParameter> paramsList)
        {
            // Filter by name
            if (!string.IsNullOrEmpty(parameters.Name))
            {
                conditions.Add("(LOWER(a.name) ILIKE LOWER(@name) OR LOWER(a.description) ILIKE LOWER(@name))");
                paramsList.Add(new NpgsqlParameter("@name", $"%{parameters.Name}%"));
            }

            // Filter by category
            if (parameters.CategoryId != null)
            {
                conditions.Add("a.category_id = @categoryId");
                paramsList.Add(new NpgsqlParameter("@categoryId", parameters.CategoryId));
            }

            // Filter by location
            if (parameters.CountryId != null)
            {
                conditions.Add("l.country_id = @countryId");
                paramsList.Add(new NpgsqlParameter("@countryId", parameters.CountryId));
            }

            if (parameters.ProvinceId != null)
            {
                conditions.Add("l.province_id = @provinceId");
                paramsList.Add(new NpgsqlParameter("@provinceId", parameters.ProvinceId));
            }

            // Filter by language
            if (parameters.LanguageIds != null && parameters.LanguageIds.Any())
            {
                conditions.Add("al.language_id = ANY(@languageIds)");
                paramsList.Add(new NpgsqlParameter("@languageIds", parameters.LanguageIds.ToArray()));
            }

            // Filter by date range - check if attraction has schedule applicable at these dates
            if (parameters.FromDate != null)
            {
                conditions.Add(@"(
                    (sch.season_start IS NULL AND sch.season_end IS NULL) OR
                    (sch.season_start IS NULL AND sch.season_end >= @fromDate) OR
                    (sch.season_start <= @fromDate AND sch.season_end IS NULL) OR
                    (sch.season_start <= @fromDate AND sch.season_end >= @fromDate)
                )");
                paramsList.Add(new NpgsqlParameter("@fromDate", parameters.FromDate));
            }

            if (parameters.ToDate != null)
            {
                conditions.Add(@"(
                    (sch.season_start IS NULL AND sch.season_end IS NULL) OR
                    (sch.season_start IS NULL AND sch.season_end >= @toDate) OR
                    (sch.season_start <= @toDate AND sch.season_end IS NULL) OR
                    (sch.season_start <= @toDate AND sch.season_end >= @toDate)
                )");
                paramsList.Add(new NpgsqlParameter("@toDate", parameters.ToDate));
            }

        }


        private static string GetSortField(string sortBy)
        {
            return sortBy.ToLower() switch
            {
                "name" => "name",
                "description" => "description",
                "created_at" => "created_at",
                "updated_at" => "updated_at",
                "id" => "id",
                "location_id" => "location_id",
                "category_id" => "category_id",
                _ => "updated_at"
            };
        }
    }
}