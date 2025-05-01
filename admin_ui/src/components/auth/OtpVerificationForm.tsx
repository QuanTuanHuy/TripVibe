"use client";

import { useState, useEffect, useRef } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/context/AuthContext';
import Link from 'next/link';

export default function OtpVerificationForm() {
  const [email, setEmail] = useState('');
  const [otpValues, setOtpValues] = useState(['', '', '', '', '', '']);
  const [error, setError] = useState('');
  const [countdown, setCountdown] = useState(30);
  const { verifyOtp, isLoading } = useAuth();
  const router = useRouter();
  const inputRefs = useRef<Array<HTMLInputElement | null>>([]);
  
  useEffect(() => {
    // Lấy email từ localStorage (lưu sau khi đăng ký)
    const storedEmail = localStorage.getItem('userEmail');
    if (!storedEmail) {
      router.push('/register'); // Nếu không có email, chuyển đến trang đăng ký
      return;
    }
    setEmail(storedEmail);
    
    // Đếm ngược 30 giây để có thể gửi lại OTP
    const timer = setInterval(() => {
      setCountdown((prev) => (prev > 0 ? prev - 1 : 0));
    }, 1000);
    
    // Focus vào input đầu tiên khi component mount
    if (inputRefs.current[0]) {
      inputRefs.current[0].focus();
    }
    
    return () => clearInterval(timer);
  }, [router]);

  const handleChange = (index: number, value: string) => {
    // Chỉ cho phép nhập số
    if (!/^\d*$/.test(value)) return;
    
    const newOtpValues = [...otpValues];
    newOtpValues[index] = value;
    setOtpValues(newOtpValues);
    
    // Nếu giá trị không rỗng và không phải ô cuối cùng, focus vào ô tiếp theo
    if (value !== '' && index < 5) {
      inputRefs.current[index + 1]?.focus();
    }
    
    // Xóa thông báo lỗi khi người dùng nhập
    if (error) setError('');
  };

  const handleKeyDown = (index: number, e: React.KeyboardEvent<HTMLInputElement>) => {
    // Nếu nhấn Backspace khi ô hiện tại trống, focus vào ô trước đó
    if (e.key === 'Backspace' && otpValues[index] === '' && index > 0) {
      inputRefs.current[index - 1]?.focus();
    }
  };

  const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
    e.preventDefault();
    const pastedData = e.clipboardData.getData('text').trim();
    
    // Nếu dữ liệu dán vào chứa đúng 6 chữ số
    if (/^\d{6}$/.test(pastedData)) {
      // Tách thành mảng các chữ số và cập nhật state
      const digits = pastedData.split('');
      setOtpValues(digits);
      
      // Focus vào ô cuối cùng
      inputRefs.current[5]?.focus();
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    const otpString = otpValues.join('');
    if (otpString.length !== 6) {
      setError('Vui lòng nhập đầy đủ mã OTP 6 chữ số');
      return;
    }
    
    try {
      await verifyOtp(email, otpString);
      // Chuyển hướng được xử lý trong AuthContext
    } catch (err: any) {
      setError(err.response?.data?.message || 'Mã OTP không đúng. Vui lòng thử lại.');
      // Reset OTP khi xác thực thất bại
      setOtpValues(['', '', '', '', '', '']);
      // Focus lại vào ô đầu tiên
      inputRefs.current[0]?.focus();
    }
  };
  
  const handleResendOtp = async () => {
    // Tính năng gửi lại OTP (có thể thêm sau)
    setCountdown(30);
    // Thêm logic gửi lại OTP
    alert('Đã gửi lại mã OTP vào email ' + email);
  };

  return (
    <div className="w-full max-w-md p-6">
      <h1 className="text-2xl font-bold text-center mb-6">Xác thực tài khoản</h1>
      
      <p className="mb-4 text-sm text-center">
        Chúng tôi đã gửi mã xác thực 6 chữ số đến email <strong>{email}</strong>.
        <br />Vui lòng nhập mã để hoàn tất quá trình đăng ký.
      </p>
      
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4 text-sm">
          {error}
        </div>
      )}
      
      <form onSubmit={handleSubmit} className="mb-6">
        <div className="mb-6">
          <label className="block text-sm mb-4 text-center">Mã xác thực OTP (6 chữ số)</label>
          
          {/* 6 ô nhập OTP */}
          <div className="flex justify-center gap-2">
            {otpValues.map((digit, index) => (
              <input
                key={index}
                ref={(el) => {
                  inputRefs.current[index] = el;
                }}
                type="text"
                inputMode="numeric"
                maxLength={1}
                value={digit}
                onChange={(e) => handleChange(index, e.target.value)}
                onKeyDown={(e) => handleKeyDown(index, e)}
                onPaste={index === 0 ? handlePaste : undefined}
                className={`w-12 h-12 text-center text-xl font-bold border ${
                  error ? 'border-red-500' : 'border-gray-300'
                } rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500`}
                aria-label={`Digit ${index + 1} of OTP`}
              />
            ))}
          </div>
          
          <p className="text-xs text-center mt-2 text-gray-500">
            Nếu bạn nhận được mã qua email, bạn có thể copy và paste vào đây
          </p>
        </div>
        
        <button 
          type="submit"
          disabled={isLoading || otpValues.join('').length !== 6}
          className={`w-full bg-blue-500 text-white p-3 rounded font-medium mb-4 ${
            isLoading || otpValues.join('').length !== 6 ? 'opacity-70 cursor-not-allowed' : 'hover:bg-blue-600 cursor-pointer'
          }`}
        >
          {isLoading ? 'Đang xác thực...' : 'Xác nhận'}
        </button>
      </form>
      
      <div className="text-center mb-6">
        <p className="text-sm mb-2">Không nhận được mã?</p>
        {countdown > 0 ? (
          <p className="text-sm text-gray-500">
            Gửi lại mã sau <span className="font-medium">{countdown}s</span>
          </p>
        ) : (
          <button 
            onClick={handleResendOtp}
            className="text-blue-500 text-sm hover:underline"
          >
            Gửi lại mã xác thực
          </button>
        )}
      </div>
      
      <div className="text-center">
        <Link href="/login" className="text-blue-500 text-sm hover:underline">
          Quay lại trang đăng nhập
        </Link>
      </div>
    </div>
  );
}