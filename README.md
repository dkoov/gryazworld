# GryazWorld

Сайт + бэкенд + Discord-бот для Minecraft сервера GryazWorld.

## Структура проекта

```
backend/                — API сервер (Python, FastAPI)
frontend/               — Сайт (React + Vite)
discord-bot/            — Discord бот (Python, discord.py)
minecraft-plugin-prod/  — Spigot плагин (Java, Maven)
nginx/                  — Конфиг nginx для хост-машины
docker-compose.yml      — Docker Compose для деплоя
```

## Как всё связано

```
Minecraft плагин ──HTTP──> Backend (FastAPI, порт 8000) <──HTTP── Frontend (React)
                               │
                               └──webhook──> Discord-бот (порт 5000) ──> Discord канал
```

---

## Часть 1: Локальная разработка

### Требования

- **Python 3.10+**
- **Node.js 18+** и **npm**

```bash
python3 --version
node --version
```

### 1. Бэкенд

```bash
cd backend

python3 -m venv .venv
source .venv/bin/activate        # Linux / Mac
# .venv\Scripts\activate         # Windows

pip install -r requirements.txt

# Создать .env
cat > .env << 'EOF'
DATABASE_URL=sqlite+aiosqlite:///./serverpanel.db
DISCORD_CLIENT_ID=твой_client_id
DISCORD_CLIENT_SECRET=твой_client_secret
DISCORD_REDIRECT_URI=http://localhost:5173/cabinet
MC_SERVER_HOST=play.gryazworld.ru
MC_SERVER_PORT=25565
PLUGIN_SECRET=любой_секрет
EOF

uvicorn main:app --reload --port 8000
```

Проверка: `http://localhost:8000/health` — `{"status":"ok"}`

### 2. Фронтенд

В новом терминале:

```bash
cd frontend
npm install
npm run dev
```

Сайт: `http://localhost:5173`. Запросы `/web/*` и `/mc/*` проксируются на бэкенд автоматически.

### 3. Discord-бот (опционально)

```bash
cd discord-bot
pip install -r requirements.txt
python bot.py
```

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
# Backend .env
cat > backend/.env << 'EOF'
DATABASE_URL=sqlite+aiosqlite:///./data/serverpanel.db
DISCORD_CLIENT_ID=твой_client_id
DISCORD_CLIENT_SECRET=твой_client_secret
DISCORD_REDIRECT_URI=https://gryazworld.ru/cabinet
MC_SERVER_HOST=play.gryazworld.ru
MC_SERVER_PORT=25565
PLUGIN_SECRET=твой_секрет_для_плагина
EOF
```

#### Шаг 4: Создать папку для базы данных

```bash
mkdir -p data
```

#### Шаг 5: Запустить всё

```bash
docker compose up -d --build
```

Готово! Сайт доступен на `http://твой_сервер`.

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

# Скопировать сборку в nginx
sudo mkdir -p /var/www/gryazworld
sudo cp -r dist/* /var/www/gryazworld/
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
sudo cp -r dist/* /var/www/gryazworld/

# Перезапустить бэкенд и бота
sudo systemctl restart gryazworld-backend
sudo systemctl restart gryazworld-bot
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
