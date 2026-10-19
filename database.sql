-- Курсова робота, варіант 5
-- Тема: Інформаційна система подання та перевірки студентських програмних проєктів
-- Скрипт відтворює базу даних з нуля.

DROP DATABASE IF EXISTS student_projects;
CREATE DATABASE student_projects CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE student_projects;

-- Студент
CREATE TABLE student (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name  VARCHAR(160) NOT NULL,
    group_name VARCHAR(20)  NOT NULL,
    email      VARCHAR(120) NOT NULL UNIQUE
);

-- Проєкт (тема / завдання, яке студент може реалізувати)
CREATE TABLE project (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    title       VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    technology  VARCHAR(80)  NOT NULL,
    difficulty  INT NOT NULL DEFAULT 1,
    CONSTRAINT chk_project_difficulty CHECK (difficulty BETWEEN 1 AND 5)
);

-- Подання (об'єкт життєвого циклу): ЧЕРНЕТКА -> ПОДАНО -> ПЕРЕВІРКА -> ПРИЙНЯТО
CREATE TABLE submission (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id     BIGINT NOT NULL,
    student_id     BIGINT NOT NULL,
    state          VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    repo_url       VARCHAR(255),
    submitted_date DATE,
    f1             DOUBLE NOT NULL DEFAULT 0,   -- повнота реалізації
    f2             DOUBLE NOT NULL DEFAULT 0,   -- результат перевірок
    f3             DOUBLE NOT NULL DEFAULT 0,   -- якість документації
    integral_index DOUBLE NOT NULL DEFAULT 0,   -- I = 0,40*F1 + 0,35*F2 + 0,25*F3
    FOREIGN KEY (project_id) REFERENCES project(id),
    FOREIGN KEY (student_id) REFERENCES student(id),
    CONSTRAINT chk_submission_state CHECK (state IN ('DRAFT', 'SUBMITTED', 'REVIEW', 'ACCEPTED')),
    CONSTRAINT chk_submission_f1 CHECK (f1 BETWEEN 0 AND 100),
    CONSTRAINT chk_submission_f2 CHECK (f2 BETWEEN 0 AND 100),
    CONSTRAINT chk_submission_f3 CHECK (f3 BETWEEN 0 AND 100)
);

-- Рецензія на подання
CREATE TABLE review (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    submission_id BIGINT NOT NULL,
    reviewer      VARCHAR(120) NOT NULL,
    comment       VARCHAR(1000),
    score         DOUBLE NOT NULL,
    review_date   DATE,
    FOREIGN KEY (submission_id) REFERENCES submission(id),
    CONSTRAINT chk_review_score CHECK (score BETWEEN 0 AND 100)
);

-- Початкові демонстраційні дані
INSERT INTO student(full_name, group_name, email) VALUES
('Гаврись Єлізавета Геннадіївна', '202-ТН', 'gavrys.elizaveta@example.com'),
('Коваленко Андрій Петрович',     '202-ТН', 'kovalenko.andriy@example.com'),
('Мельник Олена Сергіївна',       '202-ТН', 'melnyk.olena@example.com');

INSERT INTO project(title, description, technology, difficulty) VALUES
('Облік бібліотечних фондів', 'Застосунок для обліку книг, читачів і видач.', 'Java/JavaFX', 3),
('Планувальник навчальних завдань', 'Система планування завдань із нагадуваннями.', 'Java/Servlet', 2),
('Монітор складу', 'Облік товарів на складі з історією операцій.', 'Java/MySQL', 4);

INSERT INTO submission(project_id, student_id, state, repo_url, submitted_date, f1, f2, f3, integral_index) VALUES
(1, 1, 'ACCEPTED',  'https://github.com/example/library-app',  '2026-10-05', 90, 85, 80, 85.75),
(2, 2, 'REVIEW',    'https://github.com/example/task-planner', '2026-10-12', 70, 60, 50, 61.50),
(3, 3, 'SUBMITTED', 'https://github.com/example/stock-monitor','2026-10-15', 55,  0, 30, 29.50),
(1, 2, 'DRAFT',     NULL,                                      NULL,          0,  0,  0,  0.00);

INSERT INTO review(submission_id, reviewer, comment, score, review_date) VALUES
(1, 'Заїка С.О.', 'Усі вимоги реалізовано, документація повна.',         88, '2026-10-08'),
(2, 'Заїка С.О.', 'Потрібно доопрацювати валідацію та тести.',            62, '2026-10-14');
