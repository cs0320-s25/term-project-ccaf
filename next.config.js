/** @type {import('next').NextConfig} */
const nextConfig = {
  images: {
    remotePatterns: [
      {
        protocol: "https",
        hostname: "i.ebayimg.com",
        pathname: "/**",
      },
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
      {
        protocol: "https",
        hostname: "example.com", 
        pathname: "/**",
      },
    ],
  },
};

module.exports = nextConfig;
