FROM node:10

RUN mkdir -p /app
WORKDIR /app
COPY package*.json /app/
RUN npm install -g @angular/cli
RUN npm install
COPY . /app/
EXPOSE 4200
CMD ng serve --host 0.0.0.0 --disable-host-check
