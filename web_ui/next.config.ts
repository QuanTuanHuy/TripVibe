import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  reactStrictMode: true,
  images: {
    domains: ['cf.bstatic.com', 'images.unsplash.com'],
  },
  eslint: {
    ignoreDuringBuilds: true, // Bỏ qua lỗi ESLint khi build
  },
  typescript: {
    ignoreBuildErrors: true, // Bỏ qua lỗi TypeScript khi build
  },
};

export default nextConfig;
