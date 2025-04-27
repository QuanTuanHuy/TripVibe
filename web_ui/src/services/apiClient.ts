import axios, { AxiosInstance, AxiosResponse, AxiosRequestConfig } from 'axios';

// URL cơ sở của API Gateway
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

class ApiClient {
  private client: AxiosInstance;
  
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
    
    // Xử lý các phản hồi lỗi
    this.client.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          // Token hết hạn hoặc không hợp lệ
          localStorage.removeItem('token');
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
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