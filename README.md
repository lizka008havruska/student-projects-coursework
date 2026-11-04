# Інформаційна система подання та перевірки студентських програмних проєктів

Курсова робота з дисципліни «Кросплатформене програмування», варіант 5, група 202-ТН.

## Структура
- `database.sql` — створення БД `student_projects` з нуля + демонстраційні дані
- `server/` — Maven-проєкт (WAR): моделі, DBUtil, DAO (JDBC), Service (валідація, життєвий цикл, інтегральний показник)
- `client/` — Maven-проєкт JavaFX: моделі, `sample-data/projects.csv`
- `docs/` — схема БД (diagrams.net)

## Запуск етапу 1
1. Запустити MySQL (XAMPP) і виконати `database.sql` у phpMyAdmin.
2. `cd server && mvn compile exec:java` — перевірка JDBC-з'єднання.
3. `cd client && mvn javafx:run` — заглушка клієнта.

Для іншого користувача/пароля MySQL: `-Ddb.user=... -Ddb.password=...`.

## Етап 2: перевірка серверної логіки
1. Повторно виконати `database.sql` (демо-дані оновлено).
2. `cd server && mvn test` — 20 тестів JUnit 5 (логіка без MySQL).
3. `cd server && mvn compile exec:java -Dexec.mainClass=ua.edu.nupp.kpp.server.CrudCheck` —
   CRUD, правила Service, показник I і життєвий цикл на реальній MySQL (тестові записи видаляються).

Правила: F1, F3 виставляє викладач у стані ПЕРЕВІРКА; F2 = середня оцінка рецензій;
I = 0,40·F1 + 0,35·F2 + 0,25·F3.

## Етап 3: клієнт–серверна система
Маршрут: JavaFX → HttpStudentProjectsClient → HTTP/JSON → Servlet → Service → DAO → JDBC → MySQL.
Опис маршрутів і приклади запитів: `docs/http-api.md`.

1. Запустити MySQL (XAMPP) і виконати `database.sql`.
2. `cd server && mvn clean package` → `server/target/student-projects-server.war`.
3. Скопіювати WAR у `<TOMCAT 9>/webapps`, запустити Tomcat (`bin/catalina.bat run`).
4. Перевірити в браузері: `http://localhost:8080/student-projects-server/students` — має повернутися JSON.
5. `cd client && mvn javafx:run` — запуск JavaFX-клієнта (запускати з каталогу `client`, інакше CSV не знайдеться).

Адреса сервера в клієнті змінюється параметром `-Dapi.base=http://хост:порт/student-projects-server`.
