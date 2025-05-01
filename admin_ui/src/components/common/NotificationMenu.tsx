"use client";

import { useState, useRef, useEffect } from "react";
import Link from "next/link";
import { Bell, Check } from "lucide-react";

type Notification = {
  id: string;
  title: string;
  description: string;
  time: string;
  isRead: boolean;
  type: "booking" | "review" | "system" | "payment";
  link: string;
};

export function NotificationMenu() {
  const [isOpen, setIsOpen] = useState(false);
  const [notifications, setNotifications] = useState<Notification[]>([
    {
      id: "1",
      title: "Đặt phòng mới",
      description: "Nguyễn Văn B đã đặt phòng Deluxe từ 20/05 - 22/05",
      time: "10 phút trước",
      isRead: false,
      type: "booking",
      link: "/bookings/123",
    },
    {
      id: "2",
      title: "Đánh giá mới",
      description: "Trần Văn C đã đánh giá 4 sao cho khách sạn của bạn",
      time: "1 giờ trước",
      isRead: false,
      type: "review",
      link: "/reviews/456",
    },
    {
      id: "3",
      title: "Thanh toán thành công",
      description: "Bạn đã nhận được khoản thanh toán 2,500,000 VND",
      time: "3 giờ trước",
      isRead: true,
      type: "payment",
      link: "/payments/789",
    },
    {
      id: "4",
      title: "Cập nhật hệ thống",
      description: "Hệ thống sẽ bảo trì từ 02:00 - 04:00 ngày 15/05",
      time: "1 ngày trước",
      isRead: true,
      type: "system",
      link: "/notifications",
    },
  ]);
  
  const menuRef = useRef<HTMLDivElement | null>(null);
  const unreadCount = notifications.filter(notif => !notif.isRead).length;

  const toggleMenu = () => setIsOpen(!isOpen);

  // Close menu when clicking outside
  useEffect(() => {
    const handleOutsideClick = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    document.addEventListener("mousedown", handleOutsideClick);
    return () => document.removeEventListener("mousedown", handleOutsideClick);
  }, []);

  // Mark notification as read
  const markAsRead = (id: string) => {
    setNotifications(notifications.map(notif => 
      notif.id === id ? { ...notif, isRead: true } : notif
    ));
  };

  // Mark all as read
  const markAllAsRead = () => {
    setNotifications(notifications.map(notif => ({ ...notif, isRead: true })));
  };

  return (
    <div className="relative" ref={menuRef}>
      <button
        onClick={toggleMenu}
        className="relative p-2 rounded-full hover:bg-gray-100 transition-colors"
        title="Thông báo"
      >
        <Bell className="h-5 w-5" />
        {unreadCount > 0 && (
          <span className="absolute top-1 right-1 flex h-4 w-4 items-center justify-center rounded-full bg-red-500 text-[10px] text-white">
            {unreadCount}
          </span>
        )}
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 md:w-96 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 z-50 max-h-[80vh] overflow-hidden flex flex-col">
          <div className="p-3 border-b border-gray-100 flex justify-between items-center">
            <h3 className="text-sm font-medium">Thông báo</h3>
            {unreadCount > 0 && (
              <button 
                onClick={markAllAsRead}
                className="text-xs text-blue-600 hover:text-blue-800"
              >
                Đánh dấu tất cả là đã đọc
              </button>
            )}
          </div>
          
          <div className="overflow-y-auto flex-grow">
            {notifications.length > 0 ? (
              <div className="py-1">
                {notifications.map((notification) => (
                  <Link
                    key={notification.id}
                    href={notification.link}
                    className={`flex px-4 py-3 hover:bg-gray-50 ${
                      !notification.isRead ? "bg-blue-50" : ""
                    }`}
                    onClick={() => markAsRead(notification.id)}
                  >
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-gray-900 mb-0.5">
                        {notification.title}
                      </p>
                      <p className="text-xs text-gray-500 truncate">
                        {notification.description}
                      </p>
                      <p className="text-xs text-gray-400 mt-1">
                        {notification.time}
                      </p>
                    </div>
                    {!notification.isRead && (
                      <div className="ml-3 flex-shrink-0">
                        <div className="h-2 w-2 rounded-full bg-blue-600"></div>
                      </div>
                    )}
                  </Link>
                ))}
              </div>
            ) : (
              <div className="py-6 text-center">
                <p className="text-sm text-gray-500">Không có thông báo nào</p>
              </div>
            )}
          </div>
          
          <div className="p-2 border-t border-gray-100 text-center">
            <Link
              href="/notifications"
              className="block w-full text-sm text-blue-600 hover:bg-gray-50 py-2 rounded"
              onClick={() => setIsOpen(false)}
            >
              Xem tất cả thông báo
            </Link>
          </div>
        </div>
      )}
    </div>
  );
}