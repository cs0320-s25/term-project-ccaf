/** @type {import('next').NextConfig} */
module.exports = {
  experimental: {
    serverActions: true,
  },
  env: {
    NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY:
      process.env.NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY,
  },
};
