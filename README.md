# GryazWorld

Сайт + бэкенд + Discord-боты + Spigot-плагин для сети Minecraft-серверов GryazWorld / Ichorix.

## Скриншоты

Главная страница:

![Главная страница](docs/screenshots/01-main.png)

Личный кабинет:

![Личный кабинет](docs/screenshots/01-lk.png)

Магазин:

![Магазин](docs/screenshots/01-shop.png)

Вики:

![Вики](docs/screenshots/01-wiki.png)

Статистика:

![Статистика](docs/screenshots/01-stats.png)

Рейтинг богатства («Ичорбексы»):

![Рейтинг богатства](docs/screenshots/01-ichorbecs.png)

## Структура проекта

```
backend/                    — API-сервер (Python, FastAPI)
  main.py                   — приложение, middleware (CORS, CSP, CSRF, rate limit), /health
  database.py               — модели SQLAlchemy и инициализация БД
  auth.py                   — X-Plugin-Secret для плагина, JWT-сессии веб-кабинета
  cron.py                   — фоновые задачи: подписки, голосования, штрафы, очередь доставки
  products.py               — серверный каталог товаров (цены с фронта не принимаются)
  yookassa_client.py        — инициализация SDK ЮKassa + whitelist IP для вебхука
  charsystem_client.py      — доступ к игровой БД charsystem (MySQL) за игровыми ролями
  routers/
    player.py               — вход/выход/смерть/чат, античит, авторизация по IP
    bank.py                 — банк со стороны игры (банкоматы, переводы, оплата штрафов)
    bank_web.py             — банк со стороны сайта (счета, доступы, счета к оплате)
    fines.py                — штрафы и варны
    court.py                — иски и их рассмотрение
    web.py                  — кабинет, профили, статистика, общины, скины
    admin.py                — админка сайта (роли, подписки, карты)
    messenger.py            — личные сообщения сайт ↔ игра
    polls.py                — голосования (веб, админка, игра)
    payments.py             — заказы, оплата через ЮKassa, вебхук
    portals.py              — порталы между серверами сети
    soundpack.py            — сборка ресурс-пака со звуком
    internal.py             — служебный API для ботов (whitelist, авторизация, IP-баны)
frontend/                   — сайт (React 19 + Vite, сборка отдаётся nginx)
  src/pages/                — страницы: главная, магазин, вики, статистика, банк,
                              суд, кабинет, общины, мессенджер, голосования, карта, админка
  src/components/           — общие компоненты, включая 3D-просмотр скина
  nginx.conf                — конфиг nginx внутри контейнера фронта (проксирует API на backend)
discord-bot/                — бот уведомлений (Python, discord.py), вебхук на порту 5000
discord-bot-main/           — основной бот (TypeScript, discord.js): заявки, тикеты,
                              суд, whitelist, синхронизация ролей, релей чата
minecraft-plugin-prod/      — Spigot/Paper-плагин ServerPanel (Java 21, Maven)
nginx/                      — конфиг nginx для хост-машины (деплой без Docker)
scripts/backup.sh           — бэкап SQLite
docs/screenshots/           — скриншоты интерфейса
docker-compose.yml          — сборка и запуск всех сервисов
```

## Возможности

Что реально реализовано в коде:

**Аккаунты**
- Вход через Discord OAuth, привязка ника Minecraft к Discord-аккаунту.
- Привязка Twitch (отдельный OAuth, отвязка).
- Сессия сайта — JWT (HS256, 7 дней) на `SESSION_SECRET`.
- Авторизация игрока в игре по IP: неизвестный IP → подтверждение через Discord-бота,
  IP-баны, срок жизни сессии.

**Профили и статистика**
- Публичный профиль игрока: ник, скин, роли, наигранное время, варны, лайки.
- Учёт времени по дням и отдельно по каждому серверу сети.
- Общая статистика сервера, онлайн, рейтинг богатства («Ичорбексы»).
- Отдача скина и головы игрока через бэкенд.

**Банк**
- Несколько счетов у игрока, основной счёт, свои названия и картинки карт.
- Переводы между игроками и между счетами, история транзакций.
- Выдача доступа к своему счёту другому игроку.
- Счета к оплате: выставить, оплатить, отклонить.
- В игре: банкоматы (`/setatm`, `/unsetatm`), меню `/bank`, админ-команда `/adminbank`.

**Штрафы, варны, суд**
- Выдача штрафа и варна из игры (`/fine`, `/warn`, `/unwarn`), дедлайн оплаты.
- Оплата штрафа с банковского счёта — из игры и с сайта.
- Крон помечает просроченные штрафы; плагин периодически проверяет их у игроков.
- Иски: подача через Discord, рассмотрение на странице «Суд»,
  одобрение с выпиской штрафа или отклонение.

**Сообщество**
- Общины: создание, описание блоками, баннер, теги, приватность, набор участников,
  роли внутри общины, приглашения (в том числе командой `/invite` в игре), кик, выход.
- Личные сообщения между игроками: сайт ↔ игра, отметка о доставке в игру.
- Голосования: с сайта и из игры, дедлайн с автозакрытием, объявление победителя.

**Магазин и оплата**
- Каталог на стороне бэкенда (`products.py`): разбан, размут, разварн,
  сезонная проходка, подписка IchoPlus на 1/2/3 месяца.
- Оплата через ЮKassa, вебхук с проверкой IP по whitelist, заказы и платежи в БД.
- Очередь доставки (`delivery_tasks`): если плагин, бот или charsystem недоступны,
  выдача повторяется кроном с ограничением по числу попыток.
- Подписки с окончанием срока — крон снимает истёкшие.

**Служебное**
- `/internal/*` для ботов (ключ `X-Api-Key`): whitelist, смена ника,
  проверка сессии, IP-баны, наигранное время, синхронизация ролей Discord.
- Игровые роли берутся из внешней БД charsystem (MySQL); если игрок ещё ни разу
  не заходил, роль запоминается и выдаётся при первом входе.
- Античит: алерты о добыче алмазов уходят в Discord.
- Порталы между серверами сети: регистрация, поиск ближайшего, телепорт.
- Сборка ресурс-пака со звуком: загрузка аудио, конвертация ffmpeg в ogg,
  пересборка `pack.zip` и sha1.
- Безопасность: заголовки HSTS/CSP/X-Frame-Options, защита от CSRF по Origin/Referer,
  ограничение частоты запросов по префиксам путей.

Страница «Карта» — iframe на внешний рендер карты (`/map/gamegraz/`, `/map/farmgame/`),
самого рендера в репозитории нет.

## Стек

**Бэкенд** (`backend/requirements.txt`, Python 3.12 в Docker):
FastAPI 0.115.0, uvicorn[standard] 0.30.6, SQLAlchemy 2.0.35 (async),
aiosqlite 0.20.0, aiomysql 0.2.0, aiohttp 3.9.5, PyJWT 2.9.0,
yookassa 3.5.0, bleach 6.1.0, python-dotenv 1.0.1.
База — SQLite; MySQL (aiomysql) используется только для чтения внешней БД charsystem.

**Фронтенд** (`frontend/package.json`, Node 20 в Docker):
React 19.2, react-router-dom 7.13, Vite 8, framer-motion 12,
lucide-react 1.23, react-markdown 10 + remark-gfm 4,
three 0.185 с @react-three/fiber 9 и @react-three/drei 10, skinview3d 3.4.
ESLint 9.

**Плагин** (`minecraft-plugin-prod/pom.xml`, Java 21):
paper-api 1.21.1-R0.1-SNAPSHOT (provided), OkHttp 4.12.0, Gson 2.10.1,
maven-shade-plugin 3.5.0 с релокацией зависимостей.

**Боты**:
- `discord-bot/` — discord.py 2.3.2, aiohttp 3.9.5, python-dotenv 1.0.1.
- `discord-bot-main/` — discord.js 14.16, TypeScript через tsx 4.19,
  discord-html-transcripts 3.3, pnpm 10.28, Node 22 в Docker.

## Как всё связано

```
   Minecraft (Paper 1.21, плагин ServerPanel)
      │  ▲
      │  │ backend → плагин: /api/ban, /api/unban, /api/unmute,
      │  │ /api/whitelist/add  (HTTP-сервер внутри плагина, ban-api.port)
      │  │
      │  └ плагин → backend: /mc/*  (заголовок X-Plugin-Secret)
      ▼
 ┌─────────────────────────────┐        ┌──────────────────────────┐
 │  Backend (FastAPI, :8000)   │◄──────►│  charsystem (MySQL,      │
 │  SQLite serverpanel.db      │  чтение│  внешний игровой сервер) │
 └───┬───────────┬─────────┬───┘        └──────────────────────────┘
     │           │         │
     │ /web/*    │         │ POST /discord/notify
     │           │         ├──────────► discord-bot        (:5000)
     ▼           │         └──────────► discord-bot-main   (:5050)
 Frontend        │
 (React, nginx)  │  боты → backend: /internal/*  (заголовок X-Api-Key)
                 │
                 └──── ЮKassa: создание платежа, вебхук /web/payments/webhook
```

Фронтенд не ходит в бэкенд напрямую по адресу: в разработке запросы `/web/*` и `/mc/*`
проксирует dev-сервер Vite, в Docker — nginx внутри контейнера фронта.

---

## Часть 1: Локальная разработка

### Требования

- **Python 3.10+** (в Docker используется 3.12)
- **Node.js 20+** и **npm** (Vite 8 не работает на Node 18)

```bash
python3 --version
node --version
```

Быстрый вариант — `make install` и `make run` в корне: поднимет и бэкенд, и фронт.
Ниже то же самое вручную.

### 1. Бэкенд

```bash
cd backend

python3 -m venv .venv
source .venv/bin/activate        # Linux / Mac
# .venv\Scripts\activate         # Windows

pip install -r requirements.txt

# Взять шаблон и заполнить
cp .env.example .env
```

Что в `backend/.env.example` и что с этим делать локально:

| Переменная | Нужна для | Локально |
|---|---|---|
| `DISCORD_CLIENT_ID`, `DISCORD_CLIENT_SECRET` | вход через Discord | из Developer Portal |
| `DISCORD_REDIRECT_URI` | вход через Discord | `http://localhost:5173/cabinet` |
| `TWITCH_CLIENT_ID`, `TWITCH_CLIENT_SECRET`, `TWITCH_REDIRECT_URI` | привязка Twitch | можно оставить пустыми |
| `PLUGIN_SECRET` | запросы плагина к `/mc/*` | любая строка, та же в config.yml плагина |
| `SESSION_SECRET` | JWT-сессии кабинета | обязательно: без него `/web/me` отдаёт 500 |
| `DATABASE_URL` | база | `sqlite+aiosqlite:///./serverpanel.db` |
| `MC_SERVER_HOST`, `MC_SERVER_PORT` | пинг сервера, адрес плагина | как есть |
| `YOOKASSA_SHOP_ID`, `YOOKASSA_SECRET_KEY`, `YOOKASSA_RETURN_URL` | оплата | без них падает только создание платежа |

Ключ для `SESSION_SECRET`:

```bash
python -c "import secrets; print(secrets.token_urlsafe(48))"
```

Эти переменные читаются кодом, но в `.env.example` их нет — локально не нужны,
на проде задавать:

- `DISCORD_BOT_URL` — куда крон шлёт уведомления боту;
- `MC_BAN_PORT` — порт HTTP-сервера внутри плагина (по умолчанию 8080);
- `DISCORD_BOT_TOKEN`, `DISCORD_GUILD_ID` — чтение ролей Discord;
- `DISCORD_AUTH_API_KEY` — ключ для `/internal/*`, без него весь раздел отдаёт 403;
- `AUTH_BOT_URL` — адрес бота авторизации (по умолчанию `http://discord-bot-auth:5001`);
- `CHARSYSTEM_MYSQL_HOST/PORT/USER/PASSWORD/DB` — внешняя БД игровых ролей.
  Значения по умолчанию указывают на боевой сервер; локально он недоступен,
  бэкенд это переживает — роли просто не показываются.

Запуск:

```bash
uvicorn main:app --reload --port 8000
```

Проверка: `http://localhost:8000/health` — `{"status":"ok"}`

### 2. Фронтенд

В новом терминале:

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

В `frontend/.env`:

- `VITE_DISCORD_CLIENT_ID` — тот же client id, что в бэкенде;
- `VITE_TWITCH_CLIENT_ID` — можно оставить пустым;
- `VITE_REDIRECT_URI` — для локального запуска `http://localhost:5173/cabinet`;
  должен совпадать с `DISCORD_REDIRECT_URI` бэкенда, иначе OAuth не сойдётся.
  Если переменную не задать, подставится боевой `https://ichorix.cc/cabinet`.
- `VITE_API_URL` — в `.env.example` его нет, но код его читает. Пустое значение
  (по умолчанию) означает «относительные пути», то есть через прокси Vite — так и надо локально.

Сайт: `http://localhost:5173`. Запросы `/web/*` и `/mc/*` проксируются на бэкенд автоматически
(`vite.config.js`, цель `http://localhost:8000`).

### 3. Discord-бот (опционально)

```bash
cd discord-bot
cp .env.example .env
pip install -r requirements.txt
python bot.py
```

Переменные: `BOT_TOKEN`, `GUILD_ID`, `FINES_CHANNEL_ID`, `EVENTS_CHANNEL_ID`,
`BACKEND_URL` (локально `http://localhost:8000`), `API_SECRET`, `WEBHOOK_PORT` (5000).

Второй бот, `discord-bot-main/`, локально обычно не нужен: у него нет `.env.example`,
а требует он около тридцати переменных — токен, id гильдии и id каналов и ролей
(заявки, суд, тикеты, стафф, голосования, релей чата), плюс `INTERNAL_API_KEY`
и `PLUGIN_SECRET` для похода в `/internal/*`. Запуск — `pnpm start`.

---

## Часть 2: Деплой на сервер

### Вариант A: Docker Compose (рекомендуется)

Самый простой способ — всё поднимается одной командой.

#### Шаг 1: Подготовить сервер

```bash
# Подключиться к серверу
ssh user@твой_сервер

# Установить Docker (Ubuntu/Debian)
sudo apt update
sudo apt install -y ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] \
  https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# Добавить себя в группу docker (чтобы не писать sudo)
sudo usermod -aG docker $USER
newgrp docker

# Проверить
docker --version
docker compose version
```

#### Шаг 2: Склонировать проект

```bash
cd /opt
sudo git clone git@github.com:dkoov/gryazworld.git
sudo chown -R $USER:$USER gryazworld
cd gryazworld
```

#### Шаг 3: Настроить переменные окружения

```bash
# Backend .env — за основу взять backend/.env.example, DATABASE_URL переопределяется
# в docker-compose.yml на ./data/serverpanel.db, том смонтирован снаружи
cp backend/.env.example backend/.env
$EDITOR backend/.env

# Боты
cp discord-bot/.env.example discord-bot/.env
$EDITOR discord-bot/.env
```

`docker compose` поднимает пять сервисов: `backend`, `frontend`, `discord-bot`,
`discord-bot-main`, `discord-bot-auth`. Для двух последних `.env` нужно написать руками —
шаблонов в репозитории нет. `discord-bot-auth` вообще собирается из готового образа
`gryazworld-discord-bot-auth:latest`, исходников в репозитории тоже нет: без него
подтверждение входа по новому IP работать не будет, остальное — будет.

#### Шаг 4: Создать папку для базы данных

```bash
mkdir -p data
```

#### Шаг 5: Запустить всё

```bash
docker compose up -d --build
```

Сайт поднимается на порту 3000 (`http://твой_сервер:3000`), бэкенд слушает только
на 127.0.0.1 — наружу его должен отдавать nginx хост-машины.

#### Полезные команды

```bash
# Посмотреть логи
docker compose logs -f              # все сервисы
docker compose logs -f backend      # только бэкенд
docker compose logs -f discord-bot  # только бот

# Перезапустить
docker compose restart

# Остановить
docker compose down

# Обновить после git pull
docker compose up -d --build

# Посмотреть статус
docker compose ps
```

---

### Вариант B: Без Docker (ручной деплой)

Если Docker не подходит — можно поставить всё руками.

#### Шаг 1: Установить зависимости на сервере

```bash
ssh user@твой_сервер

# Python + Node.js + nginx
sudo apt update
sudo apt install -y python3 python3-venv python3-pip nginx nodejs npm

# Проверить
python3 --version   # 3.10+
node --version       # 18+
```

#### Шаг 2: Склонировать и настроить

```bash
cd /opt
sudo git clone git@github.com:dkoov/gryazworld.git
sudo chown -R $USER:$USER gryazworld
cd gryazworld
```

#### Шаг 3: Запустить бэкенд

```bash
cd /opt/gryazworld/backend

python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt

# Создать .env (см. выше)

# Тестовый запуск
uvicorn main:app --host 0.0.0.0 --port 8000
# Ctrl+C чтобы остановить
```

#### Шаг 4: Сделать бэкенд systemd-сервисом

```bash
sudo tee /etc/systemd/system/gryazworld-backend.service << 'EOF'
[Unit]
Description=GryazWorld Backend
After=network.target

[Service]
User=www-data
Group=www-data
WorkingDirectory=/opt/gryazworld/backend
Environment=PATH=/opt/gryazworld/backend/.venv/bin
ExecStart=/opt/gryazworld/backend/.venv/bin/uvicorn main:app --host 127.0.0.1 --port 8000
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable gryazworld-backend
sudo systemctl start gryazworld-backend

# Проверить
sudo systemctl status gryazworld-backend
curl http://127.0.0.1:8000/health
```

#### Шаг 5: Сделать бота systemd-сервисом

```bash
cd /opt/gryazworld/discord-bot
pip install -r requirements.txt

sudo tee /etc/systemd/system/gryazworld-bot.service << 'EOF'
[Unit]
Description=GryazWorld Discord Bot
After=network.target gryazworld-backend.service

[Service]
User=www-data
Group=www-data
WorkingDirectory=/opt/gryazworld/discord-bot
ExecStart=/usr/bin/python3 bot.py
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable gryazworld-bot
sudo systemctl start gryazworld-bot
```

#### Шаг 6: Собрать и задеплоить фронтенд

```bash
cd /opt/gryazworld/frontend
npm install
npm run build

# Скопировать сборку в nginx.
# Внимание: в nginx/gryazworld.conf root — /var/www/gryazworld/dist,
# поэтому и класть надо в dist/, а не в корень.
sudo mkdir -p /var/www/gryazworld/dist
sudo cp -r dist/* /var/www/gryazworld/dist/
```

#### Шаг 7: Настроить nginx

```bash
# Скопировать конфиг
sudo cp /opt/gryazworld/nginx/gryazworld.conf /etc/nginx/sites-available/
sudo ln -sf /etc/nginx/sites-available/gryazworld.conf /etc/nginx/sites-enabled/

# Убрать дефолтный конфиг
sudo rm -f /etc/nginx/sites-enabled/default

# Проверить и перезапустить
sudo nginx -t
sudo systemctl reload nginx
```

Сайт доступен на `http://gryazworld.ru`.

---

## Настройка HTTPS (SSL)

После того как сайт работает по HTTP:

```bash
# Установить certbot
sudo apt install -y certbot python3-certbot-nginx

# Получить сертификат (nginx должен быть запущен)
sudo certbot --nginx -d gryazworld.ru -d www.gryazworld.ru

# Certbot автоматически:
# 1. Получит сертификат от Let's Encrypt
# 2. Обновит конфиг nginx
# 3. Настроит автопродление

# Проверить автопродление
sudo certbot renew --dry-run
```

После этого сайт будет работать по `https://gryazworld.ru`.

Не забудь обновить `DISCORD_REDIRECT_URI` в `backend/.env`:
```
DISCORD_REDIRECT_URI=https://gryazworld.ru/cabinet
```

И в [Discord Developer Portal](https://discord.com/developers/applications) добавить `https://gryazworld.ru/cabinet` в Redirects.

---

## Обновление проекта на сервере

### С Docker:

```bash
cd /opt/gryazworld
git pull
docker compose up -d --build
```

### Без Docker:

```bash
cd /opt/gryazworld
git pull

# Пересобрать фронтенд
cd frontend && npm install && npm run build
sudo cp -r dist/* /var/www/gryazworld/dist/

# Перезапустить бэкенд и бота
sudo systemctl restart gryazworld-backend
sudo systemctl restart gryazworld-bot
```

---

## Бэкап базы данных

База данных — SQLite файл `serverpanel.db`. В репозитории есть готовый скрипт `scripts/backup.sh`.
В корне лежит ещё один, старый `backup.sh` — он копирует файл через `cp` и не сжимает; рабочий — тот, что в `scripts/`.

### Разовый бэкап

```bash
# Установить sqlite3 если ещё нет
sudo apt install -y sqlite3

# Запустить бэкап
/opt/gryazworld/scripts/backup.sh
```

Бэкап сохраняется в `/opt/gryazworld/backups/` в формате `serverpanel_20260327_120000.db.gz`.

### Автоматический бэкап по расписанию (cron)

```bash
# Открыть crontab
crontab -e

# Добавить строку — бэкап каждые 6 часов:
0 */6 * * * /opt/gryazworld/scripts/backup.sh >> /opt/gryazworld/backups/backup.log 2>&1
```

По умолчанию хранятся последние 30 бэкапов (настраивается в `MAX_BACKUPS` внутри скрипта).

### Восстановить из бэкапа

```bash
# Остановить бэкенд
docker compose stop backend          # Docker
# или
sudo systemctl stop gryazworld-backend  # systemd

# Распаковать бэкап
gunzip /opt/gryazworld/backups/serverpanel_20260327_120000.db.gz

# Заменить базу
cp /opt/gryazworld/backups/serverpanel_20260327_120000.db /opt/gryazworld/data/serverpanel.db

# Запустить обратно
docker compose start backend
# или
sudo systemctl start gryazworld-backend
```

---

## Открыть порты (файрвол)

```bash
# Если используется ufw
sudo ufw allow 80/tcp     # HTTP
sudo ufw allow 443/tcp    # HTTPS
sudo ufw allow 22/tcp     # SSH (не забудь!)
sudo ufw allow 8000/tcp   # Backend (только если Minecraft плагин обращается извне)
sudo ufw enable
```

---

## Troubleshooting

| Проблема | Решение |
|----------|---------|
| `502 Bad Gateway` | Бэкенд не запущен. Проверь `docker compose logs backend` или `systemctl status gryazworld-backend` |
| Фронтенд не грузится | Проверь что `dist/` не пустой и nginx указывает на правильную папку |
| Discord OAuth не работает | Проверь `DISCORD_REDIRECT_URI` в `.env` — должен совпадать с тем что в Developer Portal |
| Бот не отвечает | Проверь токен бота и логи: `docker compose logs discord-bot` |
| База пустая после рестарта Docker | Убедись что volume `./data` примонтирован и `DATABASE_URL` указывает на `./data/serverpanel.db` |
## Статус проекта

Проект остановлен в августе 2026 года: инфраструктура отключена, домены не обслуживаются, сайт недоступен. Репозиторий сохранён как архив разработки. Проект полностью поднимается локально — см. «Локальный запуск».

## Масштаб

- 123 эндпоинта REST API — 63 веб, 39 игровых, 15 внутренних для ботов
- 175 коммитов
- 5 сервисов в Docker Compose
- 5 компонентов: бэкенд, фронтенд, два Discord-бота, Minecraft-плагин

## Что бы сделал иначе

**SQLite вместо PostgreSQL.** Выбрал на старте за простоту деплоя — один файл, никакой отдельной СУБД. С ростом числа таблиц и одновременных запросов от плагина, сайта и ботов ограничения на параллельную запись стали заметны. Для системы с платежами и постоянной синхронизацией из игры правильнее было брать PostgreSQL сразу.

**Автопуш кроном как костыль для чужих правок.** Часть работ на сервере вёл другой участник команды — без доступа к репозиторию и без опыта работы с git, правя файлы напрямую на проде. Каждый мой деплой давал конфликты, а его правки терялись. Решил автоматическим коммитом с сервера в отдельную ветку раз в 30 минут: правки перестали пропадать, а вливал я их уже осознанно через мерж. Костыль сработал, но правильное решение было другим — дать человеку доступ к репозиторию и потратить час на объяснение трёх команд.

**Три Discord-бота вместо одного.** Сложилось исторически: один бот перешёл вместе с купленным проектом, второй писался под свои задачи, третий отвечал за авторизацию. В итоге три отдельных сервиса, три конфига и три точки отказа там, где хватило бы одного. У одного из них к тому же не осталось исходников в репозитории — он собирался из локального образа, что делает воспроизводимость деплоя неполной.

**Переменные окружения без единого источника правды.** Часть переменных код читает, но их нет в `.env.example` — это всплыло только при попытке поднять проект с нуля. Стоило вести схему конфигурации в одном месте и валидировать её при старте приложения.
