# Інформаційна система подання та перевірки студентських програмних проєктів

Курсова робота з дисципліни «Кросплатформене програмування», варіант 5, група 202-ТН.

## Структура
- `database.sql` — створення БД `student_projects` з нуля + демонстраційні дані
- `server/` — Maven-проєкт (WAR): моделі, DBUtil, перевірка JDBC
- `client/` — Maven-проєкт JavaFX: моделі, `sample-data/projects.csv`
- `docs/` — схема БД (diagrams.net)

## Запуск етапу 1
1. Запустити MySQL (XAMPP) і виконати `database.sql` у phpMyAdmin.
2. `cd server && mvn compile exec:java` — перевірка JDBC-з'єднання.
3. `cd client && mvn javafx:run` — заглушка клієнта.

Для іншого користувача/пароля MySQL: `-Ddb.user=... -Ddb.password=...`.
