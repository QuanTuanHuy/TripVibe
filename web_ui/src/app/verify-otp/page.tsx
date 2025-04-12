"use client";

import { useState, useEffect, useRef } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';

export default function VerifyOtp() {
  const [otp, setOtp] = useState(['', '', '', '', '', '']);
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const inputRefs = useRef<Array<HTMLInputElement | null>>([]);
  const router = useRouter();

  // Lấy email từ localStorage khi component được mount
  useEffect(() => {
    const storedEmail = localStorage.getItem('userEmail');
    if (storedEmail) {
      setEmail(storedEmail);
    } else {
      // Nếu không có email, điều hướng về trang đăng nhập
      router.push('/login');
    }
    
    // Focus vào ô đầu tiên khi trang được tải
    if (inputRefs.current[0]) {
      inputRefs.current[0].focus();
    }
  }, [router]);

  // Xử lý thay đổi giá trị từng ô input OTP
  const handleChange = (index: number, value: string) => {
    // Chỉ cho phép nhập số
    if (!/^\d*$/.test(value)) return;

    const newOtp = [...otp];
    newOtp[index] = value;
    setOtp(newOtp);

    // Nếu đã nhập giá trị và không phải ô cuối cùng, chuyển focus sang ô tiếp theo
    if (value !== '' && index < 5) {
      const nextInput = inputRefs.current[index + 1];
      if (nextInput) {
        nextInput.focus();
      }
    }
  };

  // Xử lý phím backspace
  const handleKeyDown = (index: number, e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Backspace') {
      if (otp[index] === '' && index > 0) {
        const prevInput = inputRefs.current[index - 1];
        if (prevInput) {
          prevInput.focus();
        }
      }
    }
  };

  // Xử lý paste mã OTP
  const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
    e.preventDefault();
    const clipboardData = e.clipboardData.getData('text').trim();
    
    // Nếu dữ liệu có đúng 6 ký tự và toàn là số
    if (/^\d{6}$/.test(clipboardData)) {
      const newOtp = clipboardData.split('');
      setOtp(newOtp);
      
      // Focus vào ô cuối cùng
      if (inputRefs.current[5]) {
        inputRefs.current[5].focus();
      }
    }
  };

  // Xác minh mã OTP
  const verifyOtp = () => {
    const otpValue = otp.join('');
    
    // Kiểm tra OTP có đầy đủ không
    if (otpValue.length !== 6) {
      setError('Vui lòng nhập đầy đủ mã xác thực 6 chữ số');
      return;
    }

    setLoading(true);
    setError('');
    
    // Giả lập việc xác thực OTP (thành công)
    setTimeout(() => {
      setLoading(false);
      
      // Mã OTP giả định là "123456". Có thể thay đổi để test
      if (otpValue === "123456") {
        // Xác thực thành công, điều hướng về trang chủ
        router.push('/');
      } else {
        setError('Mã xác thực không chính xác. Vui lòng thử lại');
        
        // Reset OTP
        setOtp(['', '', '', '', '', '']);
        if (inputRefs.current[0]) {
          inputRefs.current[0].focus();
        }
      }
    }, 1500);
  };

  // Gửi lại mã OTP
  const resendOtp = () => {
    setError('');
    alert("Mã xác thực mới đã được gửi tới email của bạn");
  };

  return (
    <div className="min-h-screen bg-white flex flex-col text-gray-800">
      {/* Header */}
      <header className="bg-blue-800 p-4">
        <div className="container mx-auto">
          <Link href="/" className="text-white text-2xl font-bold">
            Booking.com
          </Link>
        </div>
      </header>
      
      {/* OTP Verification Form */}
      <main className="flex-grow flex justify-center items-start py-8">
        <div className="w-full max-w-md p-6">
          <h1 className="text-2xl font-bold text-center mb-6">Xác minh địa chỉ email của bạn</h1>
          
          <p className="mb-4 text-sm text-center">
            Chúng tôi đã gửi mã xác minh đến
            <br />
            <span className="font-bold">{email}</span>
          </p>
          
          <p className="mb-6 text-sm text-center">
            Vui lòng nhập mã 6 chữ số này để tiếp tục.
          </p>
          
          {/* OTP Input Fields */}
          <div className="flex justify-center gap-2 mb-6">
            {otp.map((digit, index) => (
              <input
                key={index}
                ref={(el) => {
                  inputRefs.current[index] = el;
                  return undefined;
                }}
                type="text"
                maxLength={1}
                value={digit}
                onChange={(e) => handleChange(index, e.target.value)}
                onKeyDown={(e) => handleKeyDown(index, e)}
                onPaste={index === 0 ? handlePaste : undefined}
                className={`w-12 h-12 text-center text-xl border ${
                  error ? 'border-red-500' : 'border-gray-300'
                } rounded-md`}
              />
            ))}
          </div>
          
          {/* Error message */}
          {error && (
            <div className="mb-4 text-red-500 text-sm text-center">
              {error}
            </div>
          )}
          
          {/* Verify OTP Button */}
          <button
            className={`w-full bg-blue-500 text-white p-3 rounded font-medium mb-4 ${
              loading ? 'opacity-70 cursor-not-allowed' : ''
            }`}
            onClick={verifyOtp}
            disabled={loading}
          >
            {loading ? 'Đang xác thực...' : 'Xác minh email'}
          </button>
          
          {/* Resend OTP */}
          <div className="text-center">
            <button
              onClick={resendOtp}
              className="text-blue-500 text-sm underline"
            >
              Yêu cầu mã mới
            </button>
          </div>
          
          {/* Return to Login */}
          <div className="text-center mt-4">
            <button
              onClick={() => router.push('/login')}
              className="text-blue-500 text-sm"
            >
              Quay lại trang đăng nhập
            </button>
          </div>
          
          {/* Terms */}
          <div className="text-xs text-center text-gray-500 mt-8">
            <p>Qua việc đăng nhập hoặc tạo tài khoản, bạn đồng ý với các</p>
            <p className="mb-1">
              <Link href="/terms" className="text-blue-500">Điều khoản</Link> và 
              <Link href="/privacy" className="text-blue-500"> Điều kiện</Link> cũng như 
              <Link href="/privacy-policy" className="text-blue-500"> Chính sách bảo mật</Link> và 
              <Link href="/cookies" className="text-blue-500"> Bảo mật của chúng tôi</Link>
            </p>
            <p className="mt-4">Bản quyền (2006 - 2025) - Booking.com™</p>
          </div>
        </div>
      </main>
    </div>
  );
}