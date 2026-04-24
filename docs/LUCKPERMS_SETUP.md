# LuckPerms — настройка

Команды выполняются в консоли сервера (или через `/lp` в игре от имени admin) после установки LuckPerms на все серверы.

## Создать группы

```bash
/lp creategroup police
/lp creategroup banker
/lp creategroup moder
/lp creategroup keeper
/lp creategroup admin
```

## CoreProtect

```bash
/lp group police permission set coreprotect.inspect true
/lp group moder permission set coreprotect.inspect true
/lp group moder permission set coreprotect.rollback true
/lp group admin permission set coreprotect.inspect true
/lp group admin permission set coreprotect.rollback true
/lp group admin permission set coreprotect.restore true
```

## Наручники

```bash
/lp group police permission set gryaz.handcuff true
/lp group admin permission set gryaz.handcuff true
```

## Карта

```bash
/lp group default permission set pl3xmap.hide true
```

## MiniMessage в чате

```bash
/lp group admin permission set chat.minimessage true
/lp group moder permission set chat.minimessage true
```

## Префиксы (TAB подтягивает автоматически)

```bash
/lp group admin meta setprefix 100 "<red>[Admin] "
/lp group moder meta setprefix 90 "<gold>[Moder] "
/lp group police meta setprefix 80 "<blue>[Полиция] "
/lp group banker meta setprefix 70 "<green>[Банкир] "
/lp group keeper meta setprefix 60 "<aqua>[Хранитель] "
```

---

## Синхронизация между серверами

LuckPerms использует единую БД (MySQL/MariaDB) — данные синхронизируются автоматически.  
Для ручного обновления: `/lp networksync`

## Назначить игрока в группу

```bash
/lp user <ник> parent set <группа>
```

## Открыть веб-редактор

```bash
/lp editor
```
