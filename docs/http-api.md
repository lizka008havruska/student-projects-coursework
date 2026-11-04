# HTTP/JSON-інтерфейс сервера (етап 3)

Базова адреса після розгортання WAR у Tomcat 9: `http://localhost:8080/student-projects-server`

GET — читання, POST — зміна даних (`application/x-www-form-urlencoded`, UTF-8, параметр `action`).
Відповідь завжди JSON (UTF-8). Помилка: `{"error":"текст"}` — код **400** (порушення правил Service,
некоректні параметри) або **500** (непередбачена помилка сервера).

| URL | Метод | Параметри | Відповідь |
| --- | --- | --- | --- |
| /students | GET | — | масив студентів |
| /students | POST | action=add; fullName, groupName, email | створений студент |
| /students | POST | action=update; id, fullName, groupName, email | змінений студент |
| /students | POST | action=delete; id | {"status":"ok","deleted":true} |
| /projects | GET | — | масив проєктів |
| /projects | POST | action=add / update (id) / delete (id); title, description, technology, difficulty | проєкт або {"status":"ok"} |
| /submissions | GET | — | масив подань (з F1, F2, F3, integralIndex, state) |
| /submissions | POST | action=add; projectId, studentId, repoUrl | нове подання (стан DRAFT) |
| /submissions | POST | action=update; id, projectId, studentId, repoUrl | змінене подання (лише DRAFT) |
| /submissions | POST | action=delete; id | {"status":"ok","deleted":true} |
| /submissions | POST | action=transition; id, state (DRAFT/SUBMITTED/REVIEW/ACCEPTED) | подання з новим станом |
| /submissions | POST | action=factors; id, f1, f3 | подання з перерахованим показником |
| /reviews | GET | необов'язково submissionId | масив рецензій |
| /reviews | POST | action=add; submissionId, reviewer, comment, score, reviewDate | створена рецензія |
| /reviews | POST | action=update; id, submissionId, reviewer, comment, score, reviewDate | змінена рецензія |
| /reviews | POST | action=delete; id | {"status":"ok","deleted":true} |

## Приклад JSON (подання)
```json
{"id":1,"projectId":1,"studentId":1,"state":"ACCEPTED","repoUrl":"https://github.com/example/library-app",
 "submittedDate":"2026-10-05","f1":90.0,"f2":88.0,"f3":80.0,"integralIndex":86.8}
```

## Перевірка без JavaFX (curl.exe у Windows)
```
curl.exe http://localhost:8080/student-projects-server/students

curl.exe -d "action=add&fullName=Test+Student&groupName=202-TN&email=test.student@example.com" http://localhost:8080/student-projects-server/students

curl.exe -d "action=add&fullName=&groupName=202-TN&email=bad" http://localhost:8080/student-projects-server/students

curl.exe -d "action=transition&id=4&state=ACCEPTED" http://localhost:8080/student-projects-server/submissions

curl.exe -d "action=delete&id=ID_ТЕСТОВОГО_СТУДЕНТА" http://localhost:8080/student-projects-server/students
```
Другий запит створює запис (перевірте в phpMyAdmin), третій і четвертий мають повернути код 400 із JSON-помилкою
(порожнє ім'я; заборонений перехід ЧЕРНЕТКА → ПРИЙНЯТО), п'ятий видаляє тестовий запис.

## Формат CSV (початок імпорту)
Файл `client/sample-data/projects.csv`, UTF-8, роздільник `;`, перший рядок — заголовок:
`title;description;technology;difficulty`. Приклад рядка: `Облік бібліотечних фондів;Облік книг, читачів і видач;Java/JavaFX;3`.
Рядок відхиляється, якщо стовпців не 4, порожні title або technology, або difficulty не є цілим числом
(діапазон 1–5 додатково перевіряє сервер).
