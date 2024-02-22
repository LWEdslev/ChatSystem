FROM gradle:jdk17 AS backend-build

WORKDIR /app

COPY . /app

RUN gradle build

FROM node:16.20.2 AS frontend-build

WORKDIR /app/frontend

COPY /frontend /app/frontend

RUN npm install && npm run build

FROM gradle:jdk17 AS final

WORKDIR /app

COPY --from=backend-build /app /app

COPY --from=frontend-build /app/frontend/build /app/frontend/build

EXPOSE 8080

CMD ["gradle", "run"]
