import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  reactStrictMode: true,
  images: {
    domains: ['cf.bstatic.com'],
  },
};

export default nextConfig;
