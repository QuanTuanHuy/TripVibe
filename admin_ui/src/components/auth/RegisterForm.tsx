"use client";

import { useState } from "react";
import Link from "next/link";
import { useAuth } from "@/context/AuthContext";

export default function RegisterForm() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const { register, isLoading } = useAuth();

  const handleContinueWithEmail = () => {
    if (!email) {
      setError("Vui lòng nhập địa chỉ email");
      return;
    }

    if (!/\S+@\S+\.\S+/.test(email)) {
      setError("Vui lòng nhập địa chỉ email hợp lệ");
      return;
    }

    // Hiển thị các trường thông tin còn lại
    setShowPassword(true);
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    if (!email || !password || !confirmPassword) {
      setError("Vui lòng điền đầy đủ thông tin");
      return;
    }

    if (!/\S+@\S+\.\S+/.test(email)) {
      setError("Vui lòng nhập địa chỉ email hợp lệ");
      return;
    }

    if (password.length < 6) {
      setError("Mật khẩu phải có ít nhất 6 ký tự");
      return;
    }

    if (password !== confirmPassword) {
      setError("Mật khẩu xác nhận không khớp");
      return;
    }

    try {
      await register({
        email,
        password,
        name: name || undefined,
      });
      // Chuyển hướng đến trang xác thực OTP sẽ được xử lý trong AuthContext
    } catch (err: any) {
      setError(
        err.response?.data?.meta?.message ||
          "Đăng ký thất bại. Vui lòng thử lại."
      );
    }
  };

  return (
    <div className="w-full max-w-md p-6">
      <h1 className="text-2xl font-bold text-center mb-6">Đăng ký tài khoản</h1>

      <p className="mb-4 text-sm">
        Tạo tài khoản mới để sử dụng dịch vụ của chúng tôi
      </p>

      <form onSubmit={handleRegister}>
        {/* Email input */}
        <div className="mb-4">
          <label className="block text-sm mb-2">Địa chỉ email</label>
          <input
            type="email"
            className={`w-full p-2 border ${
              error && !showPassword ? "border-red-500" : "border-gray-300"
            } rounded`}
            placeholder="Nhập địa chỉ email của bạn"
            value={email}
            onChange={(e) => {
              setEmail(e.target.value);
              if (error) setError("");
            }}
            disabled={showPassword}
          />
          {error && !showPassword && (
            <p className="text-red-500 text-xs mt-1">{error}</p>
          )}
        </div>

        {!showPassword ? (
          <button
            type="button"
            className="w-full bg-blue-500 text-white p-3 rounded font-medium mb-4"
            onClick={handleContinueWithEmail}
          >
            Tiếp tục với email
          </button>
        ) : (
          <>
            {/* Họ tên (tùy chọn) */}
            <div className="mb-4">
              <label className="block text-sm mb-2">Họ tên (tùy chọn)</label>
              <input
                type="text"
                className="w-full p-2 border border-gray-300 rounded"
                placeholder="Nhập họ tên của bạn"
                value={name}
                onChange={(e) => setName(e.target.value)}
              />
            </div>

            {/* Password input */}
            <div className="mb-4">
              <label className="block text-sm mb-2">Mật khẩu</label>
              <input
                type="password"
                className={`w-full p-2 border ${
                  error ? "border-red-500" : "border-gray-300"
                } rounded`}
                placeholder="Tối thiểu 6 ký tự"
                value={password}
                onChange={(e) => {
                  setPassword(e.target.value);
                  if (error) setError("");
                }}
              />
            </div>

            {/* Confirm Password input */}
            <div className="mb-4">
              <label className="block text-sm mb-2">Xác nhận mật khẩu</label>
              <input
                type="password"
                className={`w-full p-2 border ${
                  error ? "border-red-500" : "border-gray-300"
                } rounded`}
                placeholder="Nhập lại mật khẩu"
                value={confirmPassword}
                onChange={(e) => {
                  setConfirmPassword(e.target.value);
                  if (error) setError("");
                }}
              />
              {error && <p className="text-red-500 text-xs mt-1">{error}</p>}
            </div>

            {/* Register button */}
            <button
              type="submit"
              className={`w-full bg-blue-500 text-white p-3 rounded font-medium mb-4 ${
                isLoading ? "opacity-70 cursor-not-allowed" : "cursor-pointer"
              }`}
              disabled={isLoading}
            >
              {isLoading ? "Đang đăng ký..." : "Đăng ký"}
            </button>

            {/* Change email button */}
            <div className="text-center mb-4">
              <button
                type="button"
                onClick={() => setShowPassword(false)}
                className="text-blue-500 text-sm hover:underline"
              >
                Thay đổi email
              </button>
            </div>
          </>
        )}

        <div className="text-center mb-4">
          <p className="text-sm">
            Đã có tài khoản?{" "}
            <Link href="/login" className="text-blue-500">
              Đăng nhập
            </Link>
          </p>
        </div>
      </form>

      {/* Terms */}
      <div className="text-xs text-center text-gray-500 mt-6">
        <p>Khi đăng ký tài khoản, bạn đồng ý với các</p>
        <p className="mb-1">
          <Link href="/terms" className="text-blue-500">
            Điều khoản
          </Link>{" "}
          và
          <Link href="/privacy" className="text-blue-500">
            {" "}
            Điều kiện
          </Link>{" "}
          cũng như
          <Link href="/privacy-policy" className="text-blue-500">
            {" "}
            Chính sách bảo mật
          </Link>
        </p>
      </div>
    </div>
  );
}
