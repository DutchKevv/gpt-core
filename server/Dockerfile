FROM node:19-alpine3.16 AS base

# inner workdir
WORKDIR /app

# only copy npm files 
# this layer will only rebuild on npm module changes (package.json)
COPY server/package*.json server/.npmrc server/nodemon.json server/tsconfig.json server/tsup.config.ts ./

# install all dependencies (dev + prod)
RUN npm ci

### DEVELOPMENT ###
FROM base AS dev
COPY server/. .
CMD npm start

EXPOSE 3000

### PRODUCTION ###
FROM base AS production
COPY server/. .
RUN npm run build \
    && rm -rf ./src \
    && NODE_ENV=production npm ci \
    # && npm i -g modclean && modclean --patterns="default:*" --ignore="*makefile*" -r && npm r -g modclean \
    && npm cache clean --force \
    && rm -rf /usr/local/{lib/node{,/.npm,_modules},bin,share/man}/npm*
CMD node ./dist/server.js

EXPOSE 80
EXPOSE 443
EXPOSE 3000
### check total node_modules size
# RUN du -hs ./node_modules
