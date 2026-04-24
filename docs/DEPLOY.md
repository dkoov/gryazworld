# Деплой плагинов на ВПС

## Порядок деплоя

1. Внести изменения в ветку `Плагины`
2. Закоммитить и запушить на GitHub
3. Подключиться к ВПС: `ssh root@65.109.82.139`
4. Скопировать файлы в нужный контейнер (см. таблицу ниже)

## Пути контейнеров на ВПС

| Сервер   | Контейнер  | Путь плагинов                                   |
|----------|-----------|--------------------------------------------------|
| gamegraz | 73ec1a4d  | `/var/lib/pterodactyl/volumes/73ec1a4d.../plugins/` |
| farmserv | 53571f93  | `/var/lib/pterodactyl/volumes/53571f93.../plugins/` |
| velocity | 718abfd1  | `/var/lib/pterodactyl/volumes/718abfd1.../plugins/` |

> Точный путь уточнить командой:
> ```bash
> find /var/lib/pterodactyl/volumes -maxdepth 1 -name "73ec1a4d*"
> ```

## Копирование конфига на сервер

```bash
# Пример: обновить конфиг TAB на gamegraz
CONTAINER=$(find /var/lib/pterodactyl/volumes -maxdepth 1 -name "73ec1a4d*" -type d)
cp plugins/configs/TAB/config.yml "$CONTAINER/plugins/TAB/config.yml"
```

## Перезапуск сервера

Через панель Pterodactyl или командой:

```bash
# Отправить команду в консоль контейнера через pterodactyl CLI / wings
# Либо перезапустить через веб-панель
```

## Важно

- Файлы в репозитории и на ВПС **должны совпадать**
- После изменения конфига сервер нужно **перезагрузить** (`/reload confirm` или рестарт)
- `.jar`-файлы плагинов **не хранятся в репозитории** — только конфиги
