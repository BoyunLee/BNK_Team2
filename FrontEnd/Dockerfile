# syntax=docker/dockerfile:1
# ===== build =====
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
# npm 캐시를 BuildKit 캐시마운트로 보존
RUN --mount=type=cache,target=/root/.npm npm ci
COPY . .
RUN npm run build

# ===== serve =====
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
