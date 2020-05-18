FROM node:10 AS builder

RUN mkdir -p /app
WORKDIR /app
COPY package*.json /app/
RUN npm install
COPY . /app/
COPY src/environments/environment.prod.ts /app/src/environments/environment.ts
RUN npm run build


FROM nginx:alpine
EXPOSE 4200
COPY --from=builder /app/dist/cdss-ui /var/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf