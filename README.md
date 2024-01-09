# Запуск тестов
### СУБД MySQL:
1. Запуск контейнеров: docker-compose up.
2. Запуск приложения: java "-Dspring.datasource.url=jdbc:mysql://localhost:3306/app" "-Dspring.datasource.username=app" "-Dspring.datasource.password=pass" -jar .\artifacts\aqa-shop.jar
3. Запуск тестов: ./gradlew clean test
4. Открыть отчет Allure в браузере: ./gradlew allureServe

### СУБД PostgreSQl:
1. Запуск контейнеров: docker-compose up.
2. Запуск приложения: java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" "-Dspring.datasource.username=app" "-Dspring.datasource.password=pass" -jar .\artifacts\aqa-shop.jar
3. Запуск тестов: ./gradlew clean test "-Durl=jdbc:postgresql://localhost:5432/app"
4. Открыть отчет Allure в браузере: ./gradlew allureServe
