"use client";

import React, { useState, useRef, useEffect } from 'react';
import { Calendar, ChevronLeft, ChevronRight } from 'lucide-react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';

interface DatePickerInputProps {
  checkInDate: Date | null;
  setCheckInDate: (date: Date | null) => void;
  checkOutDate: Date | null;
  setCheckOutDate: (date: Date | null) => void;
}

const DatePickerInput: React.FC<DatePickerInputProps> = ({
  checkInDate,
  setCheckInDate,
  checkOutDate,
  setCheckOutDate
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const datePickerRef = useRef<HTMLDivElement>(null);
  const triggerRef = useRef<HTMLDivElement>(null);

  // Format dates to display
  const formatDateRange = () => {
    if (!checkInDate && !checkOutDate) return '';
    
    const formatDate = (date: Date) => {
      return date.toLocaleDateString('vi-VN', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      });
    };
    
    if (checkInDate && !checkOutDate) {
      return formatDate(checkInDate);
    }
    
    if (checkInDate && checkOutDate) {
      return `${formatDate(checkInDate)} — ${formatDate(checkOutDate)}`;
    }
    
    return '';
  };

  // Toggle datepicker visibility
  const toggleDatePicker = () => {
    setIsOpen(!isOpen);
  };

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

  // Handle click outside to close datepicker
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        datePickerRef.current && 
        !datePickerRef.current.contains(event.target as Node) &&
        triggerRef.current &&
        !triggerRef.current.contains(event.target as Node)
      ) {
        setIsOpen(false);
      }
    };
    
    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }
    
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen]);

  // Handle date selection
  const handleDateChange = (dates: [Date | null, Date | null]) => {
    const [start, end] = dates;
    setCheckInDate(start);
    setCheckOutDate(end);
    
    // If both dates are selected, close the datepicker after a short delay
    if (start && end) {
      setTimeout(() => setIsOpen(false), 500);
    }
  };

  // Quick select buttons
  const quickSelect = (days: number) => {
    const start = new Date();
    const end = new Date();
    end.setDate(end.getDate() + days);
    setCheckInDate(start);
    setCheckOutDate(end);
  };

  return (
    <div className="flex-1 border-b md:border-b-0 md:border-r border-yellow-500 relative">
      <div 
        ref={triggerRef}
        className="flex items-center h-full px-4 py-3 cursor-pointer" 
        onClick={toggleDatePicker}
      >
        <Calendar className="w-6 h-6 text-gray-500 mr-2" />
        <div className="w-full h-full bg-white border-none focus:outline-none text-gray-800 placeholder-gray-500 rounded-md px-2 py-1">
          {formatDateRange() || "Ngày nhận phòng — Ngày trả phòng"}
        </div>
      </div>
      
      {/* Calendar dropdown */}
      {isOpen && (
        <div 
          ref={datePickerRef} 
          className="absolute left-0 right-0 mt-1 z-[999]"
          onClick={(e) => e.stopPropagation()}
        >
          <div 
            className="bg-white shadow-lg rounded-none border border-gray-200"
            style={{ 
              backgroundColor: 'white', 
              width: '661px', 
              position: 'absolute',
              top: '100%',
              left: '0',
              boxShadow: '0 2px 16px rgba(0,0,0,0.15)',
              padding: '16px'
            }}
          >
            {/* Month navigation */}
            <div className="mb-4 pb-2 flex justify-between items-center">
              <button 
                onClick={goToPrevMonth}
                className="p-1 rounded-full hover:bg-gray-100 text-blue-600"
                aria-label="Tháng trước"
                type="button"
                style={{ width: '32px', height: '32px' }}
              >
                <ChevronLeft size={16} />
              </button>
              <div 
                className="grid grid-cols-2 gap-4"
                style={{ width: '540px' }}
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
                style={{ width: '32px', height: '32px' }}
              >
                <ChevronRight size={16} />
              </button>
            </div>

            {/* Custom styles for calendar */}
            <style jsx global>{`
              .react-datepicker {
                border: none !important;
                display: flex !important;
                background-color: white !important;
                width: 100% !important;
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
              }
              .react-datepicker__day:hover {
                background-color: #f5f5f5 !important;
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
                background-color: white !important;
              }
              .react-datepicker__current-month {
                display: none !important;
              }
              .react-datepicker__navigation {
                display: none !important;
              }
              .react-datepicker__day--outside-month {
                color: #bebebe !important;
              }
              .react-datepicker__day--disabled {
                color: #dedede !important;
                text-decoration: line-through !important;
              }
              .react-datepicker__week {
                display: flex !important;
                justify-content: space-between !important;
              }
              /* Booking.com style for today */
              .react-datepicker__day--today {
                font-weight: bold !important;
                color: #006ce4 !important;
                background-color: transparent !important;
              }
            `}</style>

            <div className="bg-white w-full">
              <DatePicker
                selected={checkInDate}
                onChange={handleDateChange}
                startDate={checkInDate}
                endDate={checkOutDate}
                monthsShown={2}
                inline
                selectsRange
                // monthDate={currentMonth}
                minDate={new Date()}
                fixedHeight
              />
            </div>
            
            {/* Bottom quick select options - Booking.com style */}
            <div className="mt-4 pt-4 border-t border-gray-200">
              <div className="flex flex-wrap justify-between">
                {/* Left side - Date periods */}
                <div className="flex flex-wrap gap-2">
                  <button
                    className="px-3 py-2 rounded-sm bg-white border border-gray-300 text-sm hover:border-blue-500 text-gray-700"
                    onClick={() => {
                      const today = new Date();
                      const tomorrow = new Date();
                      tomorrow.setDate(today.getDate() + 1);
                      setCheckInDate(today);
                      setCheckOutDate(tomorrow);
                    }}
                    type="button"
                  >
                    1 đêm
                  </button>
                  <button
                    className="px-3 py-2 rounded-sm bg-white border border-gray-300 text-sm hover:border-blue-500 text-gray-700"
                    onClick={() => quickSelect(2)}
                    type="button"
                  >
                    2 đêm
                  </button>
                  <button
                    className="px-3 py-2 rounded-sm bg-white border border-gray-300 text-sm hover:border-blue-500 text-gray-700"
                    onClick={() => quickSelect(3)}
                    type="button"
                  >
                    3 đêm
                  </button>
                  <button
                    className="px-3 py-2 rounded-sm bg-white border border-gray-300 text-sm hover:border-blue-500 text-gray-700"
                    onClick={() => quickSelect(7)}
                    type="button"
                  >
                    7 đêm
                  </button>
                </div>

                {/* Right side - Selected range info */}
                {checkInDate && checkOutDate && (
                  <div className="flex items-center justify-end mt-2 sm:mt-0">
                    <div className="text-sm text-gray-700">
                      {Math.round((checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 60 * 60 * 24))} đêm
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default DatePickerInput;