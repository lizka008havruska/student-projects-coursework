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
