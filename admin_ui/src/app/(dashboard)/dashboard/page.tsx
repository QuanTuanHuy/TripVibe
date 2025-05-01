"use client";

import { useEffect } from 'react';
import { useAuth } from '@/context/AuthContext';
import { useRouter } from 'next/navigation';

export default function Dashboard() {
  const { isAuthenticated, isLoading } = useAuth();
  const router = useRouter();

  // Chuyển hướng đến login nếu chưa đăng nhập
  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      router.push('/login');
    }
  }, [isAuthenticated, isLoading, router]);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold">Dashboard</h1>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {/* Thống kê tổng quan */}
        <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-sm">
          <h3 className="text-lg font-medium text-gray-500 dark:text-gray-400 mb-1">Tổng số đặt phòng</h3>
          <p className="text-3xl font-bold">128</p>
          <p className="text-sm text-green-600 dark:text-green-400 mt-2">+12% so với tuần trước</p>
        </div>

        <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-sm">
          <h3 className="text-lg font-medium text-gray-500 dark:text-gray-400 mb-1">Tỷ lệ lấp đầy</h3>
          <p className="text-3xl font-bold">75%</p>
          <p className="text-sm text-green-600 dark:text-green-400 mt-2">+5% so với tuần trước</p>
        </div>

        <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-sm">
          <h3 className="text-lg font-medium text-gray-500 dark:text-gray-400 mb-1">Đánh giá trung bình</h3>
          <p className="text-3xl font-bold">4.8</p>
          <div className="flex text-yellow-400 mt-1">
            <span>★</span><span>★</span><span>★</span><span>★</span><span>★</span>
          </div>
        </div>

        <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-sm">
          <h3 className="text-lg font-medium text-gray-500 dark:text-gray-400 mb-1">Doanh thu (tháng này)</h3>
          <p className="text-3xl font-bold">45.7M đ</p>
          <p className="text-sm text-green-600 dark:text-green-400 mt-2">+18% so với tháng trước</p>
        </div>
      </div>

      {/* Các phần khác của Dashboard */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 bg-white dark:bg-gray-800 p-6 rounded-lg shadow-sm">
          <h2 className="text-xl font-semibold mb-4">Đặt phòng gần đây</h2>
          {/* Bảng đặt phòng gần đây */}
          <table className="w-full text-sm">
            <thead>
              <tr className="text-left border-b dark:border-gray-700">
                <th className="pb-3">Khách hàng</th>
                <th className="pb-3">Phòng</th>
                <th className="pb-3">Check-in</th>
                <th className="pb-3">Check-out</th>
                <th className="pb-3">Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              <tr className="border-b dark:border-gray-700">
                <td className="py-3">Nguyễn Văn A</td>
                <td className="py-3">Phòng Deluxe</td>
                <td className="py-3">15/05/2025</td>
                <td className="py-3">18/05/2025</td>
                <td className="py-3"><span className="px-2 py-1 rounded-full bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200 text-xs">Đã xác nhận</span></td>
              </tr>
              <tr className="border-b dark:border-gray-700">
                <td className="py-3">Trần Thị B</td>
                <td className="py-3">Phòng Superior</td>
                <td className="py-3">16/05/2025</td>
                <td className="py-3">20/05/2025</td>
                <td className="py-3"><span className="px-2 py-1 rounded-full bg-yellow-100 dark:bg-yellow-900 text-yellow-800 dark:text-yellow-200 text-xs">Đang chờ</span></td>
              </tr>
              <tr className="border-b dark:border-gray-700">
                <td className="py-3">Lê Văn C</td>
                <td className="py-3">Suite</td>
                <td className="py-3">20/05/2025</td>
                <td className="py-3">22/05/2025</td>
                <td className="py-3"><span className="px-2 py-1 rounded-full bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200 text-xs">Đã xác nhận</span></td>
              </tr>
            </tbody>
          </table>
        </div>

        <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-sm">
          <h2 className="text-xl font-semibold mb-4">Đánh giá mới nhất</h2>
          {/* Danh sách đánh giá mới nhất */}
          <div className="space-y-4">
            <div className="border-b dark:border-gray-700 pb-4">
              <div className="flex items-center justify-between mb-2">
                <p className="font-medium">Nguyễn Thị D</p>
                <div className="flex text-yellow-400">
                  <span>★</span><span>★</span><span>★</span><span>★</span><span>★</span>
                </div>
              </div>
              <p className="text-sm text-gray-600 dark:text-gray-300">Phòng sạch sẽ, nhân viên thân thiện, vị trí thuận tiện.</p>
              <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">28/04/2025</p>
            </div>

            <div className="border-b dark:border-gray-700 pb-4">
              <div className="flex items-center justify-between mb-2">
                <p className="font-medium">Phạm Văn E</p>
                <div className="flex text-yellow-400">
                  <span>★</span><span>★</span><span>★</span><span>★</span><span className="text-gray-300 dark:text-gray-600">★</span>
                </div>
              </div>
              <p className="text-sm text-gray-600 dark:text-gray-300">Dịch vụ tốt, nhưng bữa sáng chưa đa dạng.</p>
              <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">26/04/2025</p>
            </div>

            <div className="pb-2">
              <div className="flex items-center justify-between mb-2">
                <p className="font-medium">Trần Văn F</p>
                <div className="flex text-yellow-400">
                  <span>★</span><span>★</span><span>★</span><span>★</span><span>★</span>
                </div>
              </div>
              <p className="text-sm text-gray-600 dark:text-gray-300">Rất hài lòng với kỳ nghỉ tại đây. Sẽ quay lại lần sau!</p>
              <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">25/04/2025</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}