using System.Text;
using LocationService.Core.Domain.Dto.Request;
using Npgsql;

namespace LocationService.Infrastructure.Repository.Specification
{
    public class CountrySpecification
    {
        public static (string, List<NpgsqlParameter>) ToGetCountrySpecification(CountryParams countryParams)
        {
            var sql = new StringBuilder(@"
                SELECT c.* FROM countries c WHERE 1=1");

            var parameters = new List<NpgsqlParameter>();

            if (!string.IsNullOrEmpty(countryParams.Name))
            {
                sql.Append(" AND c.name ILIKE @name");
                parameters.Add(new NpgsqlParameter("@name", $"%{countryParams.Name}%"));
            }

            var orderBy = !string.IsNullOrEmpty(countryParams.SortBy) ? countryParams.SortBy : "name";
            var sortDirection = !string.IsNullOrEmpty(countryParams.SortOrder) ? "DESC" : "ASC";
            sql.Append($" ORDER BY c.{orderBy} {sortDirection}");

            if (countryParams.PageSize > 0)
            {
                sql.Append(" LIMIT @pageSize OFFSET @offset");
                parameters.Add(new NpgsqlParameter("@pageSize", countryParams.PageSize));
                parameters.Add(new NpgsqlParameter("@offset", countryParams.Page * countryParams.PageSize));
            }

            return (sql.ToString(), parameters);
        }

        public static (string, List<NpgsqlParameter>) ToCountCountrySpecification(CountryParams countryParams) {
            var sql = new StringBuilder(@"
                SELECT COUNT(*) FROM countries c WHERE 1=1");

            var parameters = new List<NpgsqlParameter>();

            if (!string.IsNullOrEmpty(countryParams.Name))
            {
                sql.Append(" AND c.name ILIKE @name");
                parameters.Add(new NpgsqlParameter("@name", $"%{countryParams.Name}%"));
            }

            return (sql.ToString(), parameters);
        }
    }
}