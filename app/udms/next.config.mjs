/**
 * @type {import('next').NextConfig}
 */
const nextConfig = {
  basePath: '/TestUDMS',
  // basePath: '/UDMS',
  experimental: {
    serverActions: {
      allowedOrigins: ['localhost:8083', 'localhost:8683', 'aps3.zemenbank.com', '*.zemenbank.com'],
    },
  },
};

export default nextConfig;

