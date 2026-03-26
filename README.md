# GryazWorld

Сайт + бэкенд + Discord-бот для Minecraft сервера GryazWorld.

## Структура проекта

```
backend/                — API сервер (Python, FastAPI)
frontend/               — Сайт (React + Vite)
discord-bot/            — Discord бот (Python, discord.py)
minecraft-plugin-prod/  — Spigot плагин (Java, Maven)
```

## Требования

- **Python 3.10+** — для бэкенда и бота
- **Node.js 18+** и **npm** — для фронтенда
- **Git** — для клонирования репозитория

Проверить версии:

```bash
python3 --version
node --version
npm --version
```

## Быстрый запуск (локально)

### 1. Бэкенд

```bash
cd backend

# Создать виртуальное окружение
python3 -m venv .venv

# Активировать окружение
source .venv/bin/activate        # Linux / Mac
# .venv\Scripts\activate         # Windows

# Установить зависимости
pip install -r requirements.txt

# Создать файл .env (скопируй и заполни свои значения)
cat > .env << 'EOF'
DATABASE_URL=sqlite+aiosqlite:///./serverpanel.db
DISCORD_CLIENT_ID=твой_client_id
DISCORD_CLIENT_SECRET=твой_client_secret
DISCORD_REDIRECT_URI=http://localhost:5173/cabinet
MC_SERVER_HOST=play.gryazworld.ru
MC_SERVER_PORT=25565
PLUGIN_SECRET=любой_секрет_для_авторизации_плагина
EOF

# Запустить сервер
uvicorn main:app --reload --port 8000
```

API будет доступен на `http://localhost:8000`.

При первом запуске автоматически создастся SQLite база `serverpanel.db`.

### 2. Фронтенд

Откройте **новый терминал** (бэкенд должен продолжать работать):

```bash
cd frontend

# Установить зависимости
npm install

# Запустить dev-сервер
npm run dev
```

Сайт откроется на `http://localhost:5173`.

Vite автоматически проксирует запросы `/web/*` и `/mc/*` на бэкенд (порт 8000), поэтому ничего дополнительно настраивать не нужно.

### 3. Discord-бот (опционально)

Бот нужен для отправки уведомлений о штрафах и предупреждениях в Discord. Для базовой работы сайта он не обязателен.

Откройте **ещё один терминал**:

```bash
cd discord-bot

# Можно использовать тот же .venv что и для бэкенда, или создать отдельный
pip install -r requirements.txt

# Запустить бота
python bot.py
```

Бот поднимает webhook-сервер на порту `5000`, через который бэкенд отправляет ему уведомления.

> Для работы бота нужен токен Discord-бота. Получить его можно в [Discord Developer Portal](https://discord.com/developers/applications).

## Проверка что всё работает

1. **Бэкенд** — откройте `http://localhost:8000/health` — должен вернуть `{"status":"ok"}`
2. **Фронтенд** — откройте `http://localhost:5173` — должна загрузиться главная страница
3. **Статистика** — перейдите на страницу «Статистика» — если бэкенд работает, загрузится список игроков (пустой, если база новая)
4. **Кабинет** — кнопка «Войти через Discord» должна редиректить на Discord OAuth

## Продакшен

```bash
# Собрать фронтенд
cd frontend
npm run build

# Результат сборки — в frontend/dist/
# Раздавать через nginx, проксируя /web и /mc на uvicorn (порт 8000)
```

## Как всё связано

```
Minecraft плагин ──HTTP──> Backend (FastAPI, порт 8000) <──HTTP── Frontend (React, порт 5173)
                               │
                               └──webhook──> Discord-бот (порт 5000) ──> Discord канал
```

- **Плагин** отправляет данные о входе/выходе игроков, штрафах и банковских операциях в бэкенд
- **Бэкенд** хранит всё в SQLite и отдаёт данные фронтенду через REST API
- **Фронтенд** показывает статистику, кабинет игрока (через Discord OAuth) и информацию об общинах
- **Бот** получает уведомления от бэкенда и постит их в Discord-канал
