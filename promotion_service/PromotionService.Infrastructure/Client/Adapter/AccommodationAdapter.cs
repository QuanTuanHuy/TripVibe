using System.Net.Http.Json;
using System.Text.Json;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using PromotionService.Core.Domain.Dto;
using PromotionService.Core.Domain.Dto.Response;
using PromotionService.Core.Port;

namespace PromotionService.Infrastructure.Client.Adapter
{
    public class AccommodationAdapter : IAccommodationPort
    {
        private readonly HttpClient _httpClient;
        private readonly IConfiguration _configuration;
        private readonly ILogger<AccommodationAdapter> _logger;

        public AccommodationAdapter(
            HttpClient httpClient,
            IConfiguration configuration,
            ILogger<AccommodationAdapter> logger)
        {
            _httpClient = httpClient;
            _configuration = configuration;
            _logger = logger;
        }

        public async Task<AccommodationDto?> GetAccommodationById(long accId)
        {
            try
            {
                var baseUrl = _httpClient.BaseAddress?.ToString() ?? _configuration["Services:Accommodation:BaseUrl"];
                if (string.IsNullOrEmpty(baseUrl))
                {
                    _logger.LogError("Base URL for accommodation service is not configured.");
                    return null;
                }

                var url = $"{baseUrl}/api/internal/v1/accommodations/{accId}";
                _logger.LogInformation("Requesting accommodation data from: {Url}", url);

                var response = await _httpClient.GetAsync(url);
                response.EnsureSuccessStatusCode();

                var content = await response.Content.ReadFromJsonAsync<Resource<AccommodationDto>>();
                if (content?.Data == null)
                {
                    _logger.LogWarning("Accommodation with id {AccId} not found or returned null data", accId);
                }
                return content?.Data;
            }
            catch (HttpRequestException ex)
            {
                _logger.LogError(ex, "HTTP request error while getting accommodation with id {AccId}: {Message}", accId, ex.Message);
                return null;
            }
            catch (JsonException ex)
            {
                _logger.LogError(ex, "JSON parsing error while getting accommodation with id {AccId}: {Message}", accId, ex.Message);
                return null;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Unexpected error while getting accommodation with id {AccId}: {Message}", accId, ex.Message);
                return null;
            }
        }
    }
}