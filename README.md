# Запуск тестов
### СУБД MySQL:
1. Запуск контейнеров: docker-compose up.
2. Запуск приложения: java -jar .\artifacts\aqa-shop.jar -P:jdbc.url=jdbc:mysql://localhost:3306/app -P:jdbc.user=app -P:jdbc.password=pass
3. Запуск тестов: ./gradlew clean test
4. Открыть отчет Allure в браузере: ./gradlew allureServe

### СУБД PostgreSQl:
1. Запуск контейнеров: docker-compose up.
2. Запуск приложения: java -jar .\artifacts\aqa-shop.jar -P:jdbc.url=jdbc:postgresql://localhost:5432/app -P:jdbc.user=app -P:jdbc.password=pass
3. Запуск тестов: ./gradlew clean test
4. Открыть отчет Allure в браузере: ./gradlew allureServe
