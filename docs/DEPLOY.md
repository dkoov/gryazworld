# Деплой плагинов на ВПС

## Порядок деплоя

1. Внести изменения в ветку `Плагины`
2. Закоммитить и запушить на GitHub
3. Подключиться к ВПС и задеплоить

---

## Подключение к ВПС

```bash
ssh root@65.109.82.139
```

## Получить изменения из ветки Плагины

```bash
cd /opt/gryazworld
git fetch origin
git checkout Плагины
git pull origin Плагины
```

---

## Скопировать .jar в контейнеры

```bash
# gamegraz
docker cp plugins/gamegraz/PluginName.jar 73ec1a4d:/data/plugins/

# farmserv
docker cp plugins/farmserv/PluginName.jar 53571f93:/data/plugins/

# Velocity
docker cp plugins/velocity/PluginName.jar 718abfd1:/server/plugins/
```

## Скопировать конфиг (пример TAB)

```bash
docker cp plugins/configs/TAB/config.yml 73ec1a4d:/data/plugins/TAB/config.yml
```

---

## Перезапустить серверы

```bash
docker restart 73ec1a4d
docker restart 53571f93
docker restart 718abfd1
```

---

## Важно

- Файлы в репозитории и на ВПС **должны совпадать**
- `.jar`-файлы плагинов **не хранятся в репозитории** — только конфиги
- После изменения конфига сервер нужно **перезагрузить**
