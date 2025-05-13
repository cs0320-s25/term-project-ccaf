/** @type {import('next').NextConfig} */
module.exports = {
  experimental: {
    serverActions: {},
  },
  env: {
    NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY:
      process.env.NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY,
  },
};
/** @type {import('next').NextConfig} */
const nextConfig = {
  images: {
    domains: [
      "i.ebayimg.com",
      "media-photos.depop.com",
      "di2ponv0v5otw.cloudfront.net", // Poshmark CDN
      "dtpmhvbsmffsz.cloudfront.net", // Poshmark CDN
      "images.poshmark.com",
    ],
    remotePatterns: [
      {
        protocol: "https",
        hostname: "**.depop.com",
      },
      {
        protocol: "https",
        hostname: "**.poshmark.com",
      },
      {
        protocol: "https",
        hostname: "**.cloudfront.net",
      },
    ],
  },
};

module.exports = nextConfig;
