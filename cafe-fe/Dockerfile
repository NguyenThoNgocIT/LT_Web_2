# Multi-stage Dockerfile for React frontend (CRA + Tailwind)
# ==== Build Stage ====
FROM node:18-alpine AS build
WORKDIR /usr/src/app
ENV CI=true
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

# ==== Nginx Runtime Stage ====
FROM nginx:1.27-alpine AS runtime
# Copy custom nginx config if provided (we will create default below)
COPY --from=build /usr/src/app/build /usr/share/nginx/html
# Replace default nginx.conf to enable SPA routing
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 CMD wget -qO- http://localhost/ || exit 1
