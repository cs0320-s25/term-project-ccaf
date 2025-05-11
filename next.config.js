/** @type {import('next').NextConfig} */
module.exports = {
  experimental: {
    serverActions: {}
  },
  env: {
    NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY:
      process.env.NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY,
  },
};
/** @type {import('next').NextConfig} */
const nextConfig = {
  images: {
    domains: ["i.ebayimg.com"],
  },
};

module.exports = nextConfig;