# Этап сборки: используем официальный образ Maven с JDK 11
FROM maven:3.8.6-openjdk-11-slim AS build
WORKDIR /app
# Копируем файл pom.xml и исходники
COPY pom.xml .
COPY src ./src
# Собираем проект, пропуская тесты
RUN mvn clean package -DskipTests

# Этап выполнения: используем легковесный образ JRE
FROM openjdk:11-jre-slim
WORKDIR /app
# Копируем скомпилированный jar из предыдущего этапа
COPY --from=build /app/target/spring-user-banking-0.0.1.jar app.jar
# Открываем порт приложения
EXPOSE 8080
# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
