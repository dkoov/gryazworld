# LuckPerms — настройка

## Режим работы

LuckPerms используется на всех серверах (gamegraz, farmserv) с синхронизацией через **единую БД**.

Рекомендуемый storage-method: `mysql` или `mariadb`.

## Конфиг (config.yml)

```yaml
storage-method: mysql

data:
  address: localhost:3306
  database: luckperms
  username: luckperms
  password: ПАРОЛЬ
  table-prefix: lp_
```

## Группы

| Группа    | Описание                  |
|-----------|---------------------------|
| default   | Все игроки по умолчанию   |
| vip       | VIP-игроки                |
| moder     | Модераторы                |
| admin     | Администраторы            |

## Синхронизация между серверами

При использовании MySQL данные автоматически синхронизируются.  
Для ручного обновления: `/lp networksync`

## Основные команды

```
/lp user <ник> parent set <группа>
/lp user <ник> permission set <право> true/false
/lp group <группа> permission set <право> true
/lp editor   — открыть веб-редактор
```
