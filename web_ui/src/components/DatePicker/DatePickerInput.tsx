"use client";

import React, { useState, useRef, useEffect } from 'react';
import { ChevronLeft, ChevronRight, X } from 'lucide-react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';

interface DatePickerInputProps {
  checkInDate: Date | null;
  setCheckInDate: (date: Date | null) => void;
  checkOutDate: Date | null;
  setCheckOutDate: (date: Date | null) => void;
  containerRef?: React.RefObject<HTMLDivElement>;
  closeCalendar?: () => void;
}

const DatePickerInput: React.FC<DatePickerInputProps> = ({
  checkInDate,
  setCheckInDate,
  checkOutDate,
  setCheckOutDate,
  closeCalendar
}) => {
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const datePickerRef = useRef<HTMLDivElement>(null);

  // Sử dụng ngày hiện tại và thời gian cố định để tương thích với Booking.com
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  // Ngày mai theo định dạng của Booking.com
  const tomorrow = new Date(today);
  tomorrow.setDate(tomorrow.getDate() + 1);

  // Ngày kết thúc mặc định (thường là ngày mai + 1)
  const defaultEndDate = new Date(tomorrow);
  defaultEndDate.setDate(defaultEndDate.getDate() + 1);

  // Thiết lập tháng hiển thị dựa trên ngày check-in đã chọn
  useEffect(() => {
    if (checkInDate) {
      setCurrentMonth(new Date(checkInDate));
    }
  }, [checkInDate]);

  // Format dates to display
  // const formatDateRange = () => {
  //   if (!checkInDate && !checkOutDate) return '';

  //   const formatDate = (date: Date) => {
  //     return date.toLocaleDateString('vi-VN', {
  //       day: '2-digit',
  //       month: '2-digit',
  //       year: 'numeric'
  //     });
  //   };

  //   if (checkInDate && !checkOutDate) {
  //     return formatDate(checkInDate);
  //   }

  //   if (checkInDate && checkOutDate) {
  //     return `${formatDate(checkInDate)} — ${formatDate(checkOutDate)}`;
  //   }

  //   return '';
  // };

  // Navigate to previous month
  const goToPrevMonth = (e: React.MouseEvent) => {
    e.stopPropagation();
    const prevMonth = new Date(currentMonth);
    prevMonth.setMonth(prevMonth.getMonth() - 1);
    setCurrentMonth(prevMonth);
  };

  // Navigate to next month
  const goToNextMonth = (e: React.MouseEvent) => {
    e.stopPropagation();
    const nextMonth = new Date(currentMonth);
    nextMonth.setMonth(nextMonth.getMonth() + 1);
    setCurrentMonth(nextMonth);
  };

  // Calculate the next month for display
  const getNextMonth = () => {
    const nextMonth = new Date(currentMonth);
    nextMonth.setMonth(nextMonth.getMonth() + 1);
    return nextMonth;
  };

  // Format the month and year for display
  const formatMonthYear = (date: Date) => {
    return date.toLocaleDateString('vi-VN', {
      month: 'long',
      year: 'numeric'
    });
  };

  // Quick select buttons (chọn nhanh)
  const quickSelect = (days: number) => {
    const start = new Date();
    start.setHours(0, 0, 0, 0);
    const end = new Date(start);
    end.setDate(end.getDate() + days);
    setCheckInDate(start);
    setCheckOutDate(end);
  };

  // Calculate number of nights
  const calculateNights = () => {
    if (!checkInDate || !checkOutDate) return 0;
    const diffTime = checkOutDate.getTime() - checkInDate.getTime();
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  };

  // Format check-in date display
  const formatCheckInDate = () => {
    if (!checkInDate) return "Thứ..., ngày/tháng/năm";

    const weekDay = checkInDate.toLocaleDateString('vi-VN', { weekday: 'short' });
    const date = checkInDate.toLocaleDateString('vi-VN', {
      day: 'numeric',
      month: 'numeric',
      year: 'numeric'
    });

    return `${weekDay}, ${date}`;
  };

  // Format check-out date display
  const formatCheckOutDate = () => {
    if (!checkOutDate) return "Thứ..., ngày/tháng/năm";

    const weekDay = checkOutDate.toLocaleDateString('vi-VN', { weekday: 'short' });
    const date = checkOutDate.toLocaleDateString('vi-VN', {
      day: 'numeric',
      month: 'numeric',
      year: 'numeric'
    });

    return `${weekDay}, ${date}`;
  };

  // Xử lý sự kiện khi chọn ngày
  const handleDateChange = (dates: [Date | null, Date | null]) => {
    const [start, end] = dates;

    // Nếu đang chọn ngày đầu tiên
    if (start && !end) {
      setCheckInDate(start);
      // Booking.com không tự động set ngày checkout khi chỉ chọn ngày checkin
      setCheckOutDate(null);
    }
    // Nếu đã chọn cả hai ngày
    else if (start && end) {
      setCheckInDate(start);
      setCheckOutDate(end);

      // Đóng calendar sau khi chọn đủ cả hai ngày (tùy chọn)
      if (closeCalendar && typeof closeCalendar === 'function') {
        // Thêm một khoảng delay nhỏ để người dùng có thể thấy kết quả chọn
        setTimeout(() => {
          closeCalendar();
        }, 300);
      }
    }
  };

  // Kiểm tra xem ngày có phải là ngày checkin không
  const isCheckInDate = (date: Date) => {
    if (!checkInDate) return false;
    return date.getDate() === checkInDate.getDate() &&
      date.getMonth() === checkInDate.getMonth() &&
      date.getFullYear() === checkInDate.getFullYear();
  };

  // Kiểm tra xem ngày có phải là ngày checkout không
  const isCheckOutDate = (date: Date) => {
    if (!checkOutDate) return false;
    return date.getDate() === checkOutDate.getDate() &&
      date.getMonth() === checkOutDate.getMonth() &&
      date.getFullYear() === checkOutDate.getFullYear();
  };

  // Custom day renderer để thêm nhãn "Nhận phòng" và "Trả phòng"
  const renderDayContents = (day: number, date: Date) => {
    return (
      <div className="day-container">
        <span className="day-number">{day}</span>
        {isCheckInDate(date) && <span className="day-label">Nhận phòng</span>}
        {isCheckOutDate(date) && <span className="day-label">Trả phòng</span>}
      </div>
    );
  };

  return (
    <div className="w-full">
      {/* Calendar */}
      <div className="bg-white w-full" ref={datePickerRef}>
        <div
          className="bg-white rounded-md p-4"
          style={{ maxWidth: '660px', width: '100%' }}
        >
          {/* Header với Close button - Booking.com style */}
          <div className="flex justify-between items-center mb-4">
            <h3 className="text-lg font-bold text-gray-800">Bạn sẽ ở đây khi nào?</h3>
            {closeCalendar && (
              <button
                onClick={closeCalendar}
                className="p-1 rounded-full hover:bg-gray-100 text-gray-500"
                aria-label="Đóng"
              >
                <X size={20} />
              </button>
            )}
          </div>

          {/* Month navigation */}
          <div className="mb-4 pb-2 flex justify-between items-center">
            <button
              onClick={goToPrevMonth}
              className="p-1 rounded-full hover:bg-gray-100 text-blue-600"
              aria-label="Tháng trước"
              type="button"
            >
              <ChevronLeft size={20} />
            </button>
            <div
              className="grid grid-cols-2 gap-4"
              style={{ width: '100%', maxWidth: '540px' }}
            >
              <div className="text-center">
                <h4 className="font-bold text-base text-gray-900">{formatMonthYear(currentMonth)}</h4>
              </div>
              <div className="text-center">
                <h4 className="font-bold text-base text-gray-900">{formatMonthYear(getNextMonth())}</h4>
              </div>
            </div>
            <button
              onClick={goToNextMonth}
              className="p-1 rounded-full hover:bg-gray-100 text-blue-600"
              aria-label="Tháng sau"
              type="button"
            >
              <ChevronRight size={20} />
            </button>
          </div>

          {/* Lịch chọn ngày */}
          <div className="mb-4">
            {/* Hiển thị ngày đã chọn ở trên cùng - kiểu Booking.com */}
            <div className="flex mb-4 bg-[#f5f5f5] p-3 rounded-md">
              <div className="flex-1 border-r border-gray-300 pr-6">
                <div className="text-xs text-gray-500 font-medium mb-1">NHẬN PHÒNG</div>
                <div className={`font-medium ${checkInDate ? 'text-gray-900' : 'text-gray-400'}`}>
                  {formatCheckInDate()}
                </div>
              </div>
              <div className="flex-1 pl-6">
                <div className="text-xs text-gray-500 font-medium mb-1">TRẢ PHÒNG</div>
                <div className={`font-medium ${checkOutDate ? 'text-gray-900' : 'text-gray-400'}`}>
                  {formatCheckOutDate()}
                </div>
                {checkInDate && checkOutDate && (
                  <div className="text-xs text-gray-500 mt-1">
                    {calculateNights()} đêm
                  </div>
                )}
              </div>
            </div>

            {/* Custom styles for calendar */}
            <style jsx global>{`
              /* DatePicker styles */
              .react-datepicker {
                border: none !important;
                display: flex !important;
                background-color: white !important;
                width: 100% !important;
                font-family: inherit !important;
              }
              .react-datepicker__month-container {
                margin: 0 !important;
                background-color: white !important;
                width: 50% !important;
                padding: 0 4px !important;
              }
              .react-datepicker__day--selected,
              .react-datepicker__day--in-selecting-range,
              .react-datepicker__day--in-range {
                background-color: #006ce4 !important;
                color: white !important;
                border-radius: 0 !important;
              }
              .react-datepicker__day--in-selecting-range:not(.react-datepicker__day--selecting-range-start):not(.react-datepicker__day--selecting-range-end),
              .react-datepicker__day--in-range:not(.react-datepicker__day--range-start):not(.react-datepicker__day--range-end) {
                background-color: #ebf3ff !important;
                color: #006ce4 !important;
              }
              .react-datepicker__day--range-start,
              .react-datepicker__day--selecting-range-start,
              .react-datepicker__day--range-end,
              .react-datepicker__day--selecting-range-end {
                background-color: #006ce4 !important;
                color: white !important;
                border-radius: 0 !important;
                position: relative !important;
                z-index: 1 !important;
                font-weight: bold !important;
              }
              .react-datepicker__day {
                margin: 0 !important;
                width: 40px !important;
                line-height: 40px !important;
                height: 40px !important;
                border-radius: 0 !important;
                font-weight: 400 !important;
                color: #262626 !important;
                font-size: 14px !important;
                cursor: pointer !important;
                border: none !important;
                position: relative !important;
              }
              .react-datepicker__day:hover {
                background-color: rgba(0, 108, 228, 0.1) !important;
                color: #006ce4 !important;
                border-radius: 0 !important;
              }
              .react-datepicker__day--keyboard-selected {
                background-color: transparent !important;
                color: inherit !important;
              }
              .react-datepicker__day-name {
                color: #6b6b6b !important;
                width: 40px !important;
                line-height: 24px !important;
                font-weight: bold !important;
                margin: 0 !important;
                font-size: 12px !important;
                text-transform: uppercase !important;
              }
              .react-datepicker__header {
                background-color: white !important;
                border-bottom: none !important;
                padding-top: 8px !important;
                padding-bottom: 8px !important;
              }
              .react-datepicker__month {
                margin: 0 !important;
              }
              .react-datepicker__current-month {
                display: none !important;
              }
              .react-datepicker__navigation {
                display: none !important;
              }
              .react-datepicker__day--outside-month {
                color: #bebebe !important;
                pointer-events: auto !important;
              }
              .react-datepicker__day--disabled {
                color: #dedede !important;
                text-decoration: line-through !important;
                pointer-events: none !important;
                opacity: 0.5 !important;
              }
              .react-datepicker__week {
                display: flex !important;
                justify-content: space-between !important;
              }
              /* Booking.com style for today */
              .react-datepicker__day--today {
                font-weight: bold !important;
                color: #006ce4 !important;
                position: relative !important;
              }
              .react-datepicker__day--today::after {
                content: "" !important;
                position: absolute !important;
                bottom: 5px !important;
                left: 50% !important;
                transform: translateX(-50%) !important;
                width: 4px !important;
                height: 4px !important;
                background-color: #006ce4 !important;
                border-radius: 50% !important;
              }
              /* Fix for month container width */
              .react-datepicker__month-container:first-child {
                border-right: 1px solid #f5f5f5;
              }
              /* Booking.com style for labels "Nhận phòng" and "Trả phòng" */
              .day-container {
                display: flex !important;
                flex-direction: column !important;
                align-items: center !important;
                position: relative !important;
                height: 100% !important;
              }
              .day-number {
                z-index: 2 !important;
              }
              .day-label {
                position: absolute !important;
                bottom: 2px !important;
                font-size: 8px !important;
                line-height: 1 !important;
                color: white !important;
                z-index: 2 !important;
              }
              /* Responsive adjustments */
              @media (max-width: 680px) {
                .react-datepicker {
                  flex-direction: column !important;
                }
                .react-datepicker__month-container {
                  width: 100% !important;
                }
                .react-datepicker__month-container:first-child {
                  border-right: none !important;
                  border-bottom: 1px solid #f5f5f5 !important;
                  margin-bottom: 16px !important;
                  padding-bottom: 16px !important;
                }
              }
            `}</style>

            <DatePicker
              selected={checkInDate}
              startDate={checkInDate}
              endDate={checkOutDate}
              monthsShown={2}
              inline
              selectsRange
              minDate={today}
              onChange={handleDateChange}
              openToDate={currentMonth}
              renderDayContents={renderDayContents}
              calendarStartDay={1} // Tuần bắt đầu từ thứ 2 theo kiểu châu Âu
              fixedHeight
              shouldCloseOnSelect={false}
              disabledKeyboardNavigation
              // Thêm các thuộc tính phong cách Booking.com
              showDisabledMonthNavigation={false}
              highlightDates={[today]}
            />
          </div>

          {/* Bottom quick select options - Booking.com style */}
          <div className="pt-4 border-t border-gray-200">
            <div className="flex flex-wrap justify-between">
              {/* Left side - Date periods */}
              <div className="flex flex-wrap gap-2">
                <button
                  className={`px-4 py-2 rounded-sm border text-sm hover:border-blue-500 hover:text-blue-600 ${calculateNights() === 1 ? 'border-blue-500 text-blue-600 bg-blue-50' : 'border-gray-300 text-gray-700 bg-white'
                    }`}
                  onClick={() => {
                    const start = new Date(today);
                    const end = new Date(tomorrow);
                    setCheckInDate(start);
                    setCheckOutDate(end);
                  }}
                  type="button"
                >
                  1 đêm
                </button>
                <button
                  className={`px-4 py-2 rounded-sm border text-sm hover:border-blue-500 hover:text-blue-600 ${calculateNights() === 2 ? 'border-blue-500 text-blue-600 bg-blue-50' : 'border-gray-300 text-gray-700 bg-white'
                    }`}
                  onClick={() => quickSelect(2)}
                  type="button"
                >
                  2 đêm
                </button>
                <button
                  className={`px-4 py-2 rounded-sm border text-sm hover:border-blue-500 hover:text-blue-600 ${calculateNights() === 3 ? 'border-blue-500 text-blue-600 bg-blue-50' : 'border-gray-300 text-gray-700 bg-white'
                    }`}
                  onClick={() => quickSelect(3)}
                  type="button"
                >
                  3 đêm
                </button>
                <button
                  className={`px-4 py-2 rounded-sm border text-sm hover:border-blue-500 hover:text-blue-600 ${calculateNights() === 7 ? 'border-blue-500 text-blue-600 bg-blue-50' : 'border-gray-300 text-gray-700 bg-white'
                    }`}
                  onClick={() => quickSelect(7)}
                  type="button"
                >
                  7 đêm
                </button>
              </div>

              {/* Right side - Selected range info */}
              {checkInDate && checkOutDate && (
                <div className="flex items-center justify-end mt-2 sm:mt-0">
                  <div className="text-sm text-gray-700 font-medium">
                    {calculateNights()} đêm
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Footer button - chỉ hiển thị khi đã chọn đủ hai ngày */}
          {checkInDate && checkOutDate && closeCalendar && (
            <div className="flex justify-end mt-4">
              <button
                onClick={closeCalendar}
                className="bg-blue-600 hover:bg-blue-700 text-white py-2 px-6 rounded font-medium"
              >
                Xong
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default DatePickerInput;