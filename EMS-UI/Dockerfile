FROM node:10 AS builder

# Build the distribution package
RUN mkdir -p /app
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . ./
RUN npm run build:prod


# Copy package to final image
FROM nginx:alpine
EXPOSE 4200
WORKDIR /var/share/nginx/html
COPY --from=builder /app/dist/cdss-ui ./
COPY nginx.conf /etc/nginx/nginx.conf

#Setup and execute startup script
COPY run.sh ./
RUN sed -i 's/\x0D$//' run.sh && \
  chmod +x ./run.sh
ENTRYPOINT ./run.sh