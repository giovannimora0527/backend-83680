FROM node:22 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
RUN mkdir -p /tmp/angular-dist
COPY --from=build /app/dist/ /tmp/angular-dist/
RUN sh -c 'dir=$(find /tmp/angular-dist -name index.html -print -quit | sed "s#/index.html##"); cp -a "$dir"/. /usr/share/nginx/html/'
EXPOSE 80
