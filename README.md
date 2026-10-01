# TaskFlow

Небольшой REST API для управления личными задачами на Java. Проект рассчитан на постепенное развитие: сначала базовый CRUD и хранение данных, затем фильтры и авторизация.

## Первая версия (MVP)

- Создать задачу.
- Получить одну задачу или список задач.
- Изменить задачу и её статус.
- Удалить задачу.
- Отфильтровать список по статусу.
- Сохранить данные в PostgreSQL.

Пользователей и авторизацию добавим после того, как заработает базовый API.

## Технологии

- Java 21
- Spring Boot
- Spring Web — HTTP API
- Spring Data JPA — доступ к базе
- PostgreSQL — хранение
- Bean Validation — проверка входных данных
- Flyway — миграции схемы базы
- Maven — сборка

## Модель задачи

| Поле | Тип | Правило |
|---|---|---|
| id | UUID | генерируется сервером |
| title | String | обязательно, 1–120 символов |
| description | String | необязательно, до 2000 символов |
| status | enum | `TODO`, `IN_PROGRESS`, `DONE` |
| dueDate | LocalDate | необязательно |
| createdAt | Instant | выставляется сервером |
| updatedAt | Instant | обновляется сервером |

## Черновик API

| Метод | Путь | Назначение |
|---|---|---|
| `POST` | `/api/tasks` | создать задачу |
| `GET` | `/api/tasks` | список; опционально `?status=TODO` |
| `GET` | `/api/tasks/{id}` | получить задачу |
| `PUT` | `/api/tasks/{id}` | полностью обновить задачу |
| `PATCH` | `/api/tasks/{id}/status` | поменять статус |
| `DELETE` | `/api/tasks/{id}` | удалить задачу |

### Пример создания

`POST /api/tasks`

```json
{
  "title": "Изучить Spring Boot",
  "description": "Сделать первый REST endpoint",
  "dueDate": "2026-10-15"
}
```

### Пример ответа

```json
{
  "id": "7af248dd-53a1-4dcb-b6c6-e0f940da9cc1",
  "title": "Изучить Spring Boot",
  "description": "Сделать первый REST endpoint",
  "status": "TODO",
  "dueDate": "2026-10-15",
  "createdAt": "2026-10-01T12:00:00Z",
  "updatedAt": "2026-10-01T12:00:00Z"
}
```

## Структура кода

```text
src/main/java/com/taskflow/
  TaskFlowApplication.java
  task/
    Task.java                 # JPA-сущность
    TaskStatus.java           # статусы
    TaskRepository.java       # запросы к базе
    TaskService.java          # бизнес-правила
    TaskController.java       # REST endpoints
    dto/                      # запросы и ответы API
    exception/                # обработка ошибок
src/main/resources/
  application.yml
  db/migration/               # Flyway SQL миграции
```

Поток запроса: Controller → Service → Repository → PostgreSQL. DTO отделяют формат API от сущности базы данных.

## Этапы реализации

1. Создать Maven-проект Spring Boot и проверить запуск приложения.
2. Описать задачу, статусы и DTO с валидацией.
3. Реализовать Repository, Service и CRUD endpoints.
4. Подключить PostgreSQL и первую Flyway-миграцию.
5. Добавить единый формат ошибок и фильтр по статусу.
6. После MVP — тесты, Swagger/OpenAPI, Docker Compose и авторизация.

## Решения для старта

- Одна таблица `tasks`; авторизации в MVP нет.
- Статус новой задачи по умолчанию — `TODO`.
- Неизвестный ID возвращает `404 Not Found`.
- Ошибки валидации возвращают `400 Bad Request`.
- Срок выполнения хранится как дата без времени (`LocalDate`).

## Как запустить

Требуются Java 21, Maven 3.6.3+ и Docker с Docker Compose. На этой машине сейчас обнаружены Java 8 и отсутствие команды Maven, поэтому перед запуском нужно установить JDK 21 и Maven.

1. Запустить PostgreSQL:

   ```bash
   docker compose up -d postgres
   ```

2. Запустить приложение из корня проекта:

   ```bash
   mvn spring-boot:run
   ```

По умолчанию API доступно на `http://localhost:8080`. Параметры подключения к базе можно переопределить переменными `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`; порт — переменной `PORT`.

## Примеры запросов

Создать задачу:

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Изучить Spring Boot","description":"Сделать первый REST endpoint","dueDate":"2026-10-15"}'
```

Получить все задачи или только задачи в работе:

```bash
curl http://localhost:8080/api/tasks
curl 'http://localhost:8080/api/tasks?status=IN_PROGRESS'
```

Изменить статус:

```bash
curl -X PATCH http://localhost:8080/api/tasks/<UUID>/status \
  -H 'Content-Type: application/json' \
  -d '{"status":"DONE"}'
```
