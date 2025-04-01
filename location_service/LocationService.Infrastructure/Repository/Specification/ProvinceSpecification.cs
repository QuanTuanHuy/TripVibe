using System.Text;
using LocationService.Core.Domain.Dto.Request;
using Npgsql;

namespace LocationService.Infrastructure.Repository.Specification
{
    public class ProvinceSpecification
    {
        public static (string, List<NpgsqlParameter>) ToGetProvincesSpecification(ProvinceParams provinceParams)
        {
            var sql = new StringBuilder(@"
                SELECT p.* FROM provinces p WHERE 1=1");

            var parameters = new List<NpgsqlParameter>();

            if (provinceParams.CountryId.HasValue)
            {
                sql.Append(" AND p.country_id = @countryId");
                parameters.Add(new NpgsqlParameter("@countryId", provinceParams.CountryId.Value));
            }

            if (!string.IsNullOrEmpty(provinceParams.Name))
            {
                sql.Append(" AND p.name ILIKE @name");
                parameters.Add(new NpgsqlParameter("@name", $"%{provinceParams.Name}%"));
            }

            if (!string.IsNullOrEmpty(provinceParams.Code))
            {
                sql.Append(" AND p.code ILIKE @code");
                parameters.Add(new NpgsqlParameter("@code", $"%{provinceParams.Code}%"));
            }

            var orderBy = !string.IsNullOrEmpty(provinceParams.SortBy) ? provinceParams.SortBy : "name";
            var sortDirection = !string.IsNullOrEmpty(provinceParams.SortOrder) ? "DESC" : "ASC";
            sql.Append($" ORDER BY p.{orderBy} {sortDirection}");

            if (provinceParams.PageSize > 0)
            {
                sql.Append(" LIMIT @pageSize OFFSET @offset");
                parameters.Add(new NpgsqlParameter("@pageSize", provinceParams.PageSize));
                parameters.Add(new NpgsqlParameter("@offset", provinceParams.Page * provinceParams.PageSize));
            }

            return (sql.ToString(), parameters);
        }

        public static (string, List<NpgsqlParameter>) ToCountProvincesSpecification(ProvinceParams provinceParams)
        {
            var sql = new StringBuilder(@"
                SELECT COUNT(*) FROM provinces p WHERE 1=1");

            var parameters = new List<NpgsqlParameter>();

            if (provinceParams.CountryId.HasValue)
            {
                sql.Append(" AND p.country_id = @countryId");
                parameters.Add(new NpgsqlParameter("@countryId", provinceParams.CountryId.Value));
            }

            if (!string.IsNullOrEmpty(provinceParams.Name))
            {
                sql.Append(" AND p.name ILIKE @name");
                parameters.Add(new NpgsqlParameter("@name", $"%{provinceParams.Name}%"));
            }

            if (!string.IsNullOrEmpty(provinceParams.Code))
            {
                sql.Append(" AND p.code ILIKE @code");
                parameters.Add(new NpgsqlParameter("@code", $"%{provinceParams.Code}%"));
            }

            return (sql.ToString(), parameters);
        }
    }
}