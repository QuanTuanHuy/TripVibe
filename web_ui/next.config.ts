import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  reactStrictMode: true,
  images: {
    domains: ['cf.bstatic.com', 'images.unsplash.com'],
  },
};

export default nextConfig;
