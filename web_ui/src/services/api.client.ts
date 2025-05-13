import axios, { AxiosInstance, AxiosResponse, AxiosRequestConfig } from 'axios';

// URL cơ sở của API Gateway
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
const AUTH_PATH = '/authentication_service/api/public/v1';

class ApiClient {
  private client: AxiosInstance;
  private isRefreshing = false;
  private refreshSubscribers: Array<(token: string) => void> = [];

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Thêm interceptor để tự động gắn token xác thực
    this.client.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('token');
        if (token) {
          config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Xử lý các phản hồi lỗi và refresh token
    this.client.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config;

        // Nếu lỗi là 401 và không phải là request refresh token và chưa retry
        if (error.response?.status === 401 &&
          !originalRequest._retry &&
          !originalRequest.url?.includes('/auth/refresh')) {

          if (this.isRefreshing) {
            // Đang refresh, queue các request khác
            try {
              const newToken = await new Promise<string>((resolve, reject) => {
                this.refreshSubscribers.push((token: string) => {
                  resolve(token);
                });
              });

              originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
              return this.client(originalRequest);
            } catch (err) {
              return Promise.reject(err);
            }
          }

          originalRequest._retry = true;
          this.isRefreshing = true; try {
            // Lấy refresh token từ localStorage
            const refreshToken = localStorage.getItem('refreshToken');

            if (!refreshToken) {
              // Không có refresh token, đăng xuất
              this.logout();
              return Promise.reject(error);
            }

            // Gọi API refresh token
            const response = await axios.post(
              `${API_BASE_URL}${AUTH_PATH}/auth/refresh`,
              { refreshToken },
              { headers: { 'Content-Type': 'application/json' } }
            );

            const { accessToken, refreshToken: newRefreshToken } = response.data.data;

            // Lưu token mới
            localStorage.setItem('token', accessToken);
            localStorage.setItem('refreshToken', newRefreshToken);

            // Cập nhật header cho request hiện tại
            originalRequest.headers['Authorization'] = `Bearer ${accessToken}`;

            // Thông báo cho các request đang chờ
            this.refreshSubscribers.forEach(callback => callback(accessToken));
            this.refreshSubscribers = [];

            return this.client(originalRequest);
          } catch (refreshError) {
            // Refresh token thất bại, đăng xuất
            this.logout();
            return Promise.reject(refreshError);
          } finally {
            this.isRefreshing = false;
          }
        }

        return Promise.reject(error);
      }
    );
  }
  // Private method để đăng xuất
  private logout(): void {
    // Chuyển hướng đến trang đăng nhập
    // Các thao tác xóa localStorage sẽ được xử lý khi authService.logout được gọi
    window.location.href = '/login';
  }

  // Phương thức GET
  async get<T>(endpoint: string, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<{ data: T }> = await this.client.get(endpoint, config);
    return response.data.data;
  }

  // Phương thức POST
  async post<T>(endpoint: string, data: any, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<{ data: T }> = await this.client.post(endpoint, data, config);
    return response.data.data;
  }

  // Phương thức PUT
  async put<T>(endpoint: string, data: any, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<{ data: T }> = await this.client.put(endpoint, data, config);
    return response.data.data;
  }

  // Phương thức DELETE
  async delete<T>(endpoint: string, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<{ data: T }> = await this.client.delete(endpoint, config);
    return response.data.data;
  }
}

// Export một instance mặc định để dùng trong ứng dụng
export const apiClient = new ApiClient();
export default apiClient;