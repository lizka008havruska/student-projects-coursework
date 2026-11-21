# Інформаційна система подання та перевірки студентських програмних проєктів

Курсова робота з дисципліни «Кросплатформене програмування», група 202-ТН, варіант 5.

Система дозволяє подавати студентські програмні проєкти, рецензувати їх та оцінювати за інтегральним
показником **I = 0,40·F1 + 0,35·F2 + 0,25·F3** (F1 — повнота реалізації, F2 — середня оцінка рецензій,
F3 — якість документації). Подання проходить чотири стани: ЧЕРНЕТКА → ПОДАНО → ПЕРЕВІРКА → ПРИЙНЯТО.

## Технології
Java 21, Maven, JavaFX 21 (клієнт), Servlet API 4.0.1 + Apache Tomcat 9 (сервер), JDBC + MySQL/MariaDB (XAMPP),
Jackson (JSON), JUnit 5, `java.util.logging`.

## Структура
```
database.sql        створення БД student_projects з нуля + демонстраційні дані
server/             Maven-проєкт (WAR): моделі, DAO, Service, Servlet, тести JUnit 5
client/             Maven-проєкт JavaFX: HTTP-клієнт, інтерфейс, CSV-імпорт, sample-data/
docs/               схема БД, схема життєвого циклу, схема маршруту, опис HTTP API, сценарій демонстрації
```
Маршрут виконання: JavaFX → HttpStudentProjectsClient → HTTP/JSON → Servlet → Service → DAO → JDBC → MySQL.

## 1. Підготовка MySQL
1. Запустити MySQL (і Apache для phpMyAdmin) у XAMPP.
2. У phpMyAdmin відкрити вкладку «Імпорт», вибрати `database.sql` і виконати. Буде створено базу
   `student_projects` (таблиці student, project, submission, review) і початкові записи.
   Скрипт можна виконувати повторно: він пересоздає базу.

## 2. Сервер
```
cd server
mvn clean test         # модульні тести JUnit 5 (MySQL і Tomcat не потрібні)
mvn clean package      # створює server/target/student-projects-server.war
```
Скопіювати WAR у `<Tomcat 9>/webapps` і запустити Tomcat (`bin/catalina.bat run`; потрібен `JAVA_HOME`).
Перевірка: `http://localhost:8080/student-projects-server/students` має повернути JSON.
Маршрути та приклади запитів — `docs/http-api.md`.

## 3. JavaFX-клієнт
```
cd client
mvn javafx:run
```
Запускати з каталогу `client` (відносний шлях `sample-data/...`). Адреса сервера за замовчуванням
`http://localhost:8080/student-projects-server`, змінюється параметром `-Dapi.base=...`.

## 4. Тести
```
cd server && mvn clean test      # 30 тестів: показник, нормування, життєвий цикл, валідація
cd client && mvn clean test      # 7 тестів: заголовок і рядки CSV
```

## 5. CSV-імпорт
Вкладка «Проєкти» → поле «Файл CSV» → «Імпортувати з CSV». Імпорт працює у фоні (JavaFX Task), хід виконання
показує ProgressBar, повторний запуск під час імпорту заблоковано.

Формат: UTF-8, роздільник `;`, перший рядок — заголовок `title;description;technology;difficulty`.

| Стовпець | Тип | Обов'язковий |
| --- | --- | --- |
| title | текст (унікальна назва) | так |
| description | текст | ні |
| technology | текст | так |
| difficulty | ціле 1–5 | так |

Приклад: `Облік бібліотечних фондів;Облік книг, читачів і видач;Java/JavaFX;3`.
Некоректні рядки пропускаються з причиною; проєкт із назвою, що вже існує, сервер відхиляє, тому повторний імпорт
не створює дублікатів. Файли в `client/sample-data/`: `projects.csv` (коректний), `projects_errors.csv`
(помилкові рядки), `projects_bad_header.csv` (неправильний заголовок).

## 6. Логування
Сервер і клієнт пишуть події через `java.util.logging` (Tomcat — у консоль і `logs/`). Імпорт фіксує початок,
пропущені рядки з причинами, кількість успішних записів і завершення. Паролі та токени не логуються.
