# ===== build =====
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
# .env.production 의 VITE_API_BASE=（빈값) → API 호출이 상대경로(/api/..) → nginx 가 백엔드로 프록시
RUN npm run build

# ===== serve =====
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
