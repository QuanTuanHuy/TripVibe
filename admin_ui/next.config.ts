import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  images: {
    domains: ['cf.bstatic.com', 'localhost'],
  },
  eslint: {
    ignoreDuringBuilds: true, // Bỏ qua lỗi ESLint khi build
  },
  typescript: {
    ignoreBuildErrors: true, // Bỏ qua lỗi TypeScript khi build
  },
};

export default nextConfig;
