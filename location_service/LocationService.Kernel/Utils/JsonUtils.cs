using System.Text.Json;
using System.Text.Json.Serialization;

namespace LocationService.Kernel.Utils
{
    public class JsonUtils
    {
        private readonly JsonSerializerOptions _options;

        public JsonUtils()
        {
            _options = new JsonSerializerOptions
            {
                PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
                PropertyNameCaseInsensitive = true,
                DefaultIgnoreCondition = JsonIgnoreCondition.WhenWritingNull,
                WriteIndented = false
            };
        }

        public string ToJson<T>(T obj)
        {
            try
            {
                return JsonSerializer.Serialize(obj, _options);
            }
            catch (Exception ex)
            {
                throw new InvalidOperationException("Failed to convert object to JSON", ex);
            }
        }

        public T? FromJson<T>(string json) where T : class
        {
            if (string.IsNullOrEmpty(json))
                return null;

            try
            {
                return JsonSerializer.Deserialize<T>(json, _options);
            }
            catch (Exception ex)
            {
                throw new InvalidOperationException("Failed to convert JSON to Object", ex);
            }
        }

        public List<T>? FromJsonList<T>(string json) where T : class
        {
            if (string.IsNullOrEmpty(json))
                return null;

            try
            {
                return JsonSerializer.Deserialize<List<T>>(json, _options);
            }
            catch (Exception ex)
            {
                throw new InvalidOperationException("Failed to convert JSON to List", ex);
            }
        }
    }
}