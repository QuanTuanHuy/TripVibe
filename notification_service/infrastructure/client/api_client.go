package client

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"github.com/golibs-starter/golib/log"
	"io"
	"net/http"
	"notification_service/core/domain/dto/response"
	"time"
)

// ServiceConfig chứa cấu hình cho một service cụ thể
type ServiceConfig struct {
	BaseURL     string
	Timeout     time.Duration
	MaxRetries  int
	RetryDelay  time.Duration
	AuthToken   string
	ServiceName string
}

// ApiClient được cải tiến với nhiều tính năng hơn
type ApiClient struct {
	services   map[string]ServiceConfig
	httpClient *http.Client
}

// ApiClientOption là function tùy chọn để cấu hình ApiClient
type ApiClientOption func(*ApiClient)

// NewApiClient khởi tạo ApiClient với các tùy chọn
func NewApiClient(options ...ApiClientOption) *ApiClient {
	client := &ApiClient{
		services: make(map[string]ServiceConfig),
		httpClient: &http.Client{
			Timeout: time.Second * 10,
		},
	}

	// Áp dụng các tùy chọn
	for _, option := range options {
		option(client)
	}

	return client
}

// WithService thêm một service mới vào ApiClient
func WithService(name, baseURL string, timeout time.Duration) ApiClientOption {
	return func(client *ApiClient) {
		client.services[name] = ServiceConfig{
			BaseURL:     baseURL,
			Timeout:     timeout,
			MaxRetries:  3,
			RetryDelay:  500 * time.Millisecond,
			ServiceName: name,
		}
		client.httpClient.Timeout = timeout
	}
}

// WithDefaultTimeout đặt timeout mặc định cho http client
func WithDefaultTimeout(timeout time.Duration) ApiClientOption {
	return func(client *ApiClient) {
		client.httpClient.Timeout = timeout
	}
}

// Request tạo một yêu cầu mới và thực hiện nó
func (c *ApiClient) Request(ctx context.Context, serviceName, method, endpoint string, requestBody interface{}) (*response.ApiResponse, error) {
	service, ok := c.services[serviceName]
	if !ok {
		return nil, fmt.Errorf("service %s not configured", serviceName)
	}

	url := service.BaseURL + endpoint
	log.Info("Request to %s (%s): %s", serviceName, method, url)

	return c.executeRequest(ctx, service, method, url, requestBody)
}

// executeRequest thực hiện HTTP request với logic retry
func (c *ApiClient) executeRequest(ctx context.Context, service ServiceConfig, method, url string, requestBody interface{}) (*response.ApiResponse, error) {
	var (
		res     *response.ApiResponse
		err     error
		attempt int
	)

	for attempt = 0; attempt <= service.MaxRetries; attempt++ {
		if attempt > 0 {
			log.Info("Retrying request to %s (attempt %d/%d)", url, attempt, service.MaxRetries)
			select {
			case <-ctx.Done():
				return nil, ctx.Err()
			case <-time.After(service.RetryDelay * time.Duration(attempt)):
				// Exponential backoff
			}
		}

		res, err = c.doSingleRequest(ctx, service, method, url, requestBody)
		if err == nil {
			return res, nil
		}

		// Không retry nếu lỗi không phải là tạm thời
		if !isRetryableError(err) {
			break
		}
	}

	if err != nil {
		log.Error("Request failed after %d attempts: %v", attempt, err)
	}
	return res, err
}

// doSingleRequest thực hiện một request duy nhất
func (c *ApiClient) doSingleRequest(ctx context.Context, service ServiceConfig, method, url string, requestBody interface{}) (*response.ApiResponse, error) {
	// Tạo request body JSON
	var reqBody []byte
	var err error
	if requestBody != nil {
		reqBody, err = json.Marshal(requestBody)
		if err != nil {
			return nil, fmt.Errorf("failed to marshal request body: %w", err)
		}
	}

	// Tạo HTTP request
	httpReq, err := http.NewRequestWithContext(ctx, method, url, bytes.NewBuffer(reqBody))
	if err != nil {
		return nil, fmt.Errorf("failed to create request: %w", err)
	}

	// Thêm headers
	httpReq.Header.Set("Content-Type", "application/json")
	if service.AuthToken != "" {
		httpReq.Header.Set("Authorization", "Bearer "+service.AuthToken)
	}
	httpReq.Header.Set("X-Service-Name", "booking-service")

	// Thực hiện request
	startTime := time.Now()
	resp, err := c.httpClient.Do(httpReq)
	duration := time.Since(startTime)

	if err != nil {
		log.Error("HTTP request failed in %v: %v", duration, err)
		return nil, fmt.Errorf("HTTP request failed: %w", err)
	}
	defer resp.Body.Close()

	log.Info("Response from %s: status=%d, time=%v", url, resp.StatusCode, duration)

	// Đọc và xử lý response
	respBody, err := io.ReadAll(resp.Body)
	if err != nil {
		return nil, fmt.Errorf("failed to read response body: %w", err)
	}

	// Parse JSON response
	var apiResp response.ApiResponse
	if err := json.Unmarshal(respBody, &apiResp); err != nil {
		// Nếu không phải cấu trúc ApiResponse, thử parse trực tiếp vào data field
		if resp.StatusCode >= 200 && resp.StatusCode < 300 {
			var data interface{}
			if err := json.Unmarshal(respBody, &data); err != nil {
				return nil, fmt.Errorf("failed to parse response: %w", err)
			}
			apiResp.Data = data
		} else {
			return nil, fmt.Errorf("invalid response format (status: %d): %s", resp.StatusCode, string(respBody))
		}
	}

	// Xử lý lỗi HTTP
	if resp.StatusCode >= 400 {
		errMsg := "Request failed"
		return &apiResp, fmt.Errorf("HTTP error %d: %s", resp.StatusCode, errMsg)
	}

	return &apiResp, nil
}

// GetJSON là phương thức tiện ích để thực hiện GET request
func (c *ApiClient) GetJSON(ctx context.Context, serviceName, endpoint string, result interface{}) error {
	resp, err := c.Request(ctx, serviceName, http.MethodGet, endpoint, nil)
	if err != nil {
		return err
	}

	// Chuyển đổi data thành result
	if resp.Data != nil {
		dataBytes, err := json.Marshal(resp.Data)
		if err != nil {
			return fmt.Errorf("error converting response data: %w", err)
		}
		return json.Unmarshal(dataBytes, result)
	}
	return nil
}

// PostJSON là phương thức tiện ích để thực hiện POST request
func (c *ApiClient) PostJSON(ctx context.Context, serviceName, endpoint string, body interface{}, result interface{}) error {
	resp, err := c.Request(ctx, serviceName, http.MethodPost, endpoint, body)
	if err != nil {
		return err
	}

	// Chuyển đổi data thành result
	if resp.Data != nil && result != nil {
		dataBytes, err := json.Marshal(resp.Data)
		if err != nil {
			return fmt.Errorf("error converting response data: %w", err)
		}
		return json.Unmarshal(dataBytes, result)
	}
	return nil
}

// PutJSON là phương thức tiện ích để thực hiện PUT request
func (c *ApiClient) PutJSON(ctx context.Context, serviceName, endpoint string, body interface{}, result interface{}) error {
	resp, err := c.Request(ctx, serviceName, http.MethodPut, endpoint, body)
	if err != nil {
		return err
	}

	// Chuyển đổi data thành result
	if resp.Data != nil && result != nil {
		dataBytes, err := json.Marshal(resp.Data)
		if err != nil {
			return fmt.Errorf("error converting response data: %w", err)
		}
		return json.Unmarshal(dataBytes, result)
	}
	return nil
}

// DeleteJSON là phương thức tiện ích để thực hiện DELETE request
func (c *ApiClient) DeleteJSON(ctx context.Context, serviceName, endpoint string, result interface{}) error {
	resp, err := c.Request(ctx, serviceName, http.MethodDelete, endpoint, nil)
	if err != nil {
		return err
	}

	// Chuyển đổi data thành result nếu có
	if resp.Data != nil && result != nil {
		dataBytes, err := json.Marshal(resp.Data)
		if err != nil {
			return fmt.Errorf("error converting response data: %w", err)
		}
		return json.Unmarshal(dataBytes, result)
	}
	return nil
}

// SetAuthToken cập nhật token xác thực cho service
func (c *ApiClient) SetAuthToken(serviceName, token string) error {
	service, ok := c.services[serviceName]
	if !ok {
		return fmt.Errorf("service %s not configured", serviceName)
	}
	service.AuthToken = token
	c.services[serviceName] = service
	return nil
}

// ConfigureService cập nhật cấu hình cho một service
func (c *ApiClient) ConfigureService(serviceName string, config ServiceConfig) error {
	if _, ok := c.services[serviceName]; !ok {
		return fmt.Errorf("service %s not configured", serviceName)
	}
	c.services[serviceName] = config
	return nil
}

// WithServiceRetry cấu hình retry cho một service cụ thể
func WithServiceRetry(serviceName string, maxRetries int, retryDelay time.Duration) ApiClientOption {
	return func(client *ApiClient) {
		if service, ok := client.services[serviceName]; ok {
			service.MaxRetries = maxRetries
			service.RetryDelay = retryDelay
			client.services[serviceName] = service
		}
	}
}

// AddService thêm một service mới lúc runtime
func (c *ApiClient) AddService(name, baseURL string, timeout time.Duration) {
	c.services[name] = ServiceConfig{
		BaseURL:     baseURL,
		Timeout:     timeout,
		MaxRetries:  3,
		RetryDelay:  500 * time.Millisecond,
		ServiceName: name,
	}
}

// isRetryableError kiểm tra xem lỗi có phải là tạm thời và có thể retry không
func isRetryableError(err error) bool {
	// TODO: Có thể cài đặt chi tiết hơn để chỉ retry cho các lỗi tạm thời
	// Ví dụ: kiểm tra các lỗi như timeout, connection refused, service unavailable
	// Hiện tại, mặc định retry tất cả các lỗi
	return true
}
